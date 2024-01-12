/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.util.stream.Collectors

/**
 * @since 1.1.0
 * @author Hibi_10000
 */
class JsonDB(private val gateDB: File) {

    /**
     * Read [JsonArray] in [gateDB]
     * @return [JsonArray] in File
     * @throws IOException [JsonDB(gateDB)][JsonDB] [gateDB] isn't found
     * @throws IllegalStateException The contents of File are not JsonArray
     * @throws SecurityException [Files.lines(gateDB.toPath())][Files.lines] Is security manager installed to [gateDB]?
     * @throws InvalidPathException [gateDB.toPath()][File.toPath] If [gateDB] path string cannot be converted
     * @since 1.1.0
     */
    @Throws(IOException::class, RuntimeException::class)
    fun read(): JsonArray {
        val content = Files.lines(gateDB.toPath()).use { lines ->
            lines.collect(Collectors.joining(System.lineSeparator()))
        }
        val array = Gson().fromJson(content, JsonArray::class.java)
        check(array.isJsonArray) { "The contents of File are not JsonArray" }
        return array.asJsonArray
    }

    /**
     * Write JsonArray to [gateDB]
     * @param json JsonArray to write to [gateDB]
     * @throws IOException see [FileWriter] and [FileWriter.write]
     * @since 1.1.0
     */
    @Throws(IOException::class)
    fun write(json: JsonArray) {
        val write = GsonBuilder().setPrettyPrinting().create()
        val writeToJson = write.toJson(json)
        FileWriter(gateDB, false).use { fw ->
            fw.write(writeToJson)
        }
    }

    /**
     * Get the value corresponding to the input value
     * @param id ID of the entry to get
     * @param name The name of the column you want to get
     * @return Value in place corresponding to the input value
     * @throws RuntimeException see [read]
     * @throws IOException see [read]
     * @since 1.1.0
     * @see read
     */
    @Throws(IOException::class, RuntimeException::class)
    fun get(id: String, name: String): String? {
        for (element in read()) {
            val jo = element.asJsonObject
            if (jo["id"].asString.equals(id, ignoreCase = true)) {
                return jo[name].asString
            }
        }
        return null
    }

    /**
     * Add an entry to the table
     * @param values Set the default values in the order of  (id is set automatically)
     * @throws IOException see [read]
     * @throws RuntimeException see [read]
     * @since 1.1.0
     */
    @Throws(IOException::class, RuntimeException::class)
    fun add(values: Map<String, String>) {
        var lastId = 0
        val json = read()
        for (element in json) {
            val jo = element.asJsonObject
            val id = jo["id"].asString.toInt()
            if (lastId < id) lastId = id
        }
        lastId++
        val idJo = JsonObject()
        idJo.addProperty("id", lastId.toString())
        for (value in values) {
            idJo.addProperty(value.key, value.value)
        }
        json.add(idJo)
        write(json)
    }

    /**
     * Change the value of the specified column of the specified existing entry
     * @param id Specifies the id of the entry for which you want to change the value of a column
     * @param key Specify the name of the column whose value you want to change
     * @param value Specifies a value that replaces the value in the specified column
     * @throws RuntimeException see [read]
     * @throws IOException see [read] &amp; [write]
     * @since 1.1.0
     */
    @Throws(IOException::class, RuntimeException::class)
    fun set(id: String, key: String, value: String) {
        val json = read()
        val newJson = JsonArray()
        for (element in json) {
            val oldJo = element.asJsonObject
            if (oldJo["id"].asString.equals(id, ignoreCase = true)) {
                val jo = JsonObject().asJsonObject
                for ((oldKey) in oldJo.entrySet()) {
                    if (oldKey.equals(key, ignoreCase = true)) {
                        jo.addProperty(key, value)
                    } else {
                        jo.addProperty(oldKey, oldJo[oldKey].asString)
                    }
                }
                newJson.add(jo)
            } else {
                newJson.add(oldJo)
            }
        }
        write(newJson)
    }

    /**
     * Remove entry
     * @param id ID of the entry to remove
     * @throws IllegalStateException see [JsonArray.getAsString]
     * @throws IOException see [read] &amp; [write]
     * @since 1.1.0
     */
    @Throws(IOException::class, IllegalStateException::class)
    fun remove(id: String) {
        val json = read()
        val newJson = JsonArray()
        for (element in json) {
            val jo = element.asJsonObject
            if (!jo["id"].asString.equals(id, ignoreCase = true)) {
                newJson.add(jo)
            }
        }
        write(newJson)
    }

    /**
     * Gets the first entry that matches the value of the specified key
     * @param key Specify the key to search
     * @param value Specifies the value corresponding to the specified key
     * @return Returns the ID of the retrieved entry as a String
     * @throws IOException see [read]
     * @throws IllegalStateException see [read]
     * @throws SecurityException see [read]
     * @throws InvalidPathException see [read]
     * @since 1.1.0
     */
    @Throws(IOException::class, RuntimeException::class)
    fun firstIndexOf(key: String, value: String): String {
        val json = read()
        var back = "-1"
        for (element in json) {
            val jo = element.asJsonObject
            if (jo[key].asString.equals(value, ignoreCase = true)
                && (back.toInt() > jo["id"].asString.toInt()
                        || back.equals("-1", ignoreCase = true))
            ) {
                back = jo["id"].asString
            }
        }
        return back
    }

    /**
     * Gets the last entry that matches the value of the specified key
     * @param key Specify the key to search
     * @param value Specifies the value corresponding to the specified key
     * @return Returns the ID of the retrieved entry as a String
     * @throws IOException see [read]
     * @throws IllegalStateException see [read]
     * @throws SecurityException see [read]
     * @throws InvalidPathException see [read]
     * @since 1.1.0
     */
    @Throws(IOException::class, RuntimeException::class)
    fun lastIndexOf(key: String?, value: String?): String {
        val json = read()
        var back = "-1"
        for (element in json) {
            val jo = element.asJsonObject
            if (jo[key].asString.equals(value, ignoreCase = true)
                && back.toInt() < jo["id"].asString.toInt()
            ) {
                back = jo["id"].asString
            }
        }
        return back
    }

    /**
     * Gets all entries that match the value of the specified key
     * @param key Specify the key to search
     * @param value Specifies the value corresponding to the specified key
     * @return Returns the ID of the retrieved entry as a StringList
     * @throws IOException see [read]
     * @throws IllegalStateException see [read]
     * @throws SecurityException see [read]
     * @throws InvalidPathException see [read]
     * @since 1.1.0
     */
    @Throws(IOException::class, RuntimeException::class)
    fun allIndexOf(key: String?, value: String?): List<String> {
        val json = read()
        val back: MutableList<String> = ArrayList()
        for (element in json) {
            val jo = element.asJsonObject
            if (jo[key].asString.equals(value, ignoreCase = true)) {
                back.add(jo["id"].asString)
            }
        }
        return back
    }

    /**
     * @throws RuntimeException see [write]
     * @throws IOException see [write]
     * @since 1.1.0
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    fun clear() {
        write(JsonArray())
    }
}

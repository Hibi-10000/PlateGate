/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.google.gson.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class JsonUtil(private val gateDB: File): DBUtil(gateDB) {
    /**
     * Read [JsonArray] in [gateDB]
     * @return [JsonArray] in File
     * @throws IOException [FileReader(gateDB, Charset)][FileReader] If [gateDB] isn't found or some reasons
     * @throws JsonIOException [Gson.fromJson(Reader, Class)][Gson.fromJson] If there was a problem reading from the [FileReader]
     * @throws JsonSyntaxException [Gson.fromJson(Reader, Class)][Gson.fromJson] If contents of [File] are not [JsonArray]
     * @throws IllegalStateException [JsonArray.getAsJsonArray] If contents of [File] are not [JsonArray]
     */
    @Throws(IOException::class, RuntimeException::class)
    private fun read(): JsonArray {
        val array = FileReader(gateDB, StandardCharsets.UTF_8).use { reader->
            Gson().fromJson(reader, JsonArray::class.java)
        }
        check(array.isJsonArray) { "The contents of File are not JsonArray" }
        return array.asJsonArray
    }

    /**
     * Write JsonArray to [gateDB]
     * @param json JsonArray to write to [gateDB]
     * @throws IOException [FileWriter(gateDB, Charset)][FileWriter] If [gateDB] cannot be opened
     * @throws JsonIOException [Gson.toJson(json, writer)][Gson.toJson] If there was a problem writing to the [FileWriter]
     */
    @Throws(IOException::class, RuntimeException::class)
    private fun write(json: JsonArray) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        FileWriter(gateDB, StandardCharsets.UTF_8, false).use { writer->
            gson.toJson(json, writer)
        }
    }

    @Throws(RuntimeException::class)
    private fun getLastId(json: JsonArray): Int {
        var lastId = 0
        for (element in json) {
            val jo = element.asJsonObject
            val id = jo["id"].asInt
            if (lastId < id) lastId = id
        }
        return lastId
    }

    /**
     * Add an entry to the table (id is set automatically)
     * @param plateGate [CraftPlateGate] to add
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun add(plateGate: CraftPlateGate) {
        //TODO: createDate, updateDate
        val json = read()
        if (get(json, plateGate.owner, plateGate.name) != null) {
            throw GateNameDuplicateException()
        }
        val idJo = JsonObject()
        idJo.addProperty("id", getLastId(json) + 1)
        idJo.addProperty("owner", plateGate.owner.toString())
        idJo.addProperty("name", plateGate.name)
        idJo.addProperty("toOwner", plateGate.toOwner?.toString())
        idJo.addProperty("toName", plateGate.toName)
        idJo.addProperty("world", plateGate.world.toString())
        idJo.addProperty("x", plateGate.x)
        idJo.addProperty("y", plateGate.y)
        idJo.addProperty("z", plateGate.z)
        idJo.addProperty("rotate", plateGate.rotate.name.lowercase())
        idJo.addProperty("beforeBlock", plateGate.beforeBlock.name)
        json.add(idJo)
        write(json)
    }

    /**
     * Get [CraftPlateGate] from name and owner
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to get
     * @return [CraftPlateGate] corresponding to the input values
     * @throws IOException see [read]
     * @throws RuntimeException see [read]
     * @see read
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun get(owner: UUID, name: String): CraftPlateGate {
        val json = read()
        return get(json, owner, name) ?: throw GateNotFoundException()
    }

    @Throws(RuntimeException::class)
    private fun get(json: JsonArray, owner: UUID, name: String): CraftPlateGate? {
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["name"].asString == name && jo["owner"].asString == owner.toString()) {
                return CraftPlateGate(jo)
            }
        }
        return null
    }

    /**
     * Get [CraftPlateGate] from coordinates
     * @param world World-specific [UUID]
     * @param x X-axis of coordinates
     * @param y Y-axis of coordinates
     * @param z Z-axis of coordinates
     * @return [CraftPlateGate] corresponding to the input value
     * @throws IOException see [read]
     * @throws RuntimeException see [read]
     * @see read
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun get(world: UUID, x: Int, y: Int, z: Int): CraftPlateGate {
        val json = read()
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["world"].asString == world.toString() && jo["x"].asInt == x && jo["y"].asInt == y && jo["z"].asInt == z) {
                return CraftPlateGate(jo)
            }
        }
        throw GateNotFoundException()
    }

    /**
     * Get a list of [CraftPlateGate] owned by the player
     * @param owner Player-specific [UUID] of the gate owner
     * @return List of [CraftPlateGate] owned by the player
     * @throws IOException see [read]
     * @throws RuntimeException see [read]
     * @see read
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun getList(owner: UUID): List<CraftPlateGate> {
        val json = read()
        val list = mutableListOf<CraftPlateGate>()
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["owner"].asString == owner.toString()) {
                list.add(CraftPlateGate(jo))
            }
        }
        return list
    }

    /**
     * Link the gate to another gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to link
     * @param toOwner Player-specific [UUID] string of the gate owner to link to
     * @param toName PlateGate Name to link
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun link(owner: UUID, name: String, toOwner: UUID, toName: String) {
        val json = read()
        if (get(json, toOwner, toName) != null) {
            for (element in json) {
                val jo = element.asJsonObject
                if (jo["name"].asString == name && jo["owner"].asString == owner.toString()) {
                    jo.addProperty("toName", toName)
                    jo.addProperty("toOwner", toOwner.toString())
                    json[json.indexOf(element)] = jo
                    write(json)
                    return
                }
            }
        }
        throw GateNotFoundException()
    }

    /**
     * Move the gate to another location
     * @param plateGate [CraftPlateGate] to move
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun move(plateGate: CraftPlateGate) {
        val json = read()
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["name"].asString == plateGate.name && jo["owner"].asString == plateGate.owner.toString()) {
                jo.addProperty("world", plateGate.world.toString())
                jo.addProperty("x", plateGate.x)
                jo.addProperty("y", plateGate.y)
                jo.addProperty("z", plateGate.z)
                jo.addProperty("rotate", plateGate.rotate.name.lowercase())
                jo.addProperty("beforeBlock", plateGate.beforeBlock.name)
                json[json.indexOf(element)] = jo
                write(json)
                return
            }
        }
        throw GateNotFoundException()
    }

    /**
     * Remove the gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to remove
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun remove(owner: UUID, name: String) {
        val json = read()
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["name"].asString == name && jo["owner"].asString == owner.toString()) {
                json.remove(element)
                write(json)
                return
            }
        }
        throw GateNotFoundException()
    }

    /**
     * Rename the gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to rename
     * @param newName New PlateGate Name
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun rename(owner: UUID, name: String, newName: String) {
        val json = read()
        if (get(json, owner, newName) != null) {
            throw GateNameDuplicateException()
        }
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["name"].asString == name && jo["owner"].asString == owner.toString()) {
                jo.addProperty("name", newName)
                json[json.indexOf(element)] = jo
                write(json)
                return
            }
        }
        throw GateNotFoundException()
    }

    /**
     * Transfer the gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to transfer
     * @param newOwner Player-specific [UUID] of the new gate owner
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun transfer(owner: UUID, name: String, newOwner: UUID) {
        val json = read()
        if (get(json, newOwner, name) != null) {
            throw GateNameDuplicateException()
        }
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["name"].asString == name && jo["owner"].asString == owner.toString()) {
                jo.addProperty("owner", newOwner.toString())
                json[json.indexOf(element)] = jo
                write(json)
                return
            }
        }
        throw GateNotFoundException()
    }
}

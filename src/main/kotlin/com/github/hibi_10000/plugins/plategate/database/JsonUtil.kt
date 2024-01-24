/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.util
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class JsonUtil(private val gateDB: File) {
    @Throws(IOException::class, RuntimeException::class)
    fun read(): JsonArray {
        val reader = FileReader(gateDB, StandardCharsets.UTF_8)
        val array = Gson().fromJson(reader, JsonArray::class.java)
        check(array.isJsonArray) { "The contents of File are not JsonArray" }
        return array.asJsonArray
    }

    @Throws(IOException::class)
    fun write(json: JsonArray) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val writer = FileWriter(gateDB, StandardCharsets.UTF_8, false)
        gson.toJson(json, writer)
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

    @Throws(IOException::class, RuntimeException::class)
    fun add(plateGate: CraftPlateGate) {
        //TODO: createDate, updateDate
        val json = read()
        val idJo = JsonObject()
        idJo.addProperty("id", getLastId(json) + 1)
        idJo.addProperty("name", plateGate.name)
        idJo.addProperty("owner", plateGate.owner.toString())
        idJo.addProperty("to", plateGate.to)
        idJo.addProperty("x", plateGate.x)
        idJo.addProperty("y", plateGate.y)
        idJo.addProperty("z", plateGate.z)
        idJo.addProperty("rotate", util.convBlockFace2Facing(plateGate.rotate))
        idJo.addProperty("world", plateGate.world.toString())
        idJo.addProperty("beforeBlock", plateGate.beforeBlock.name)
        json.add(idJo)
        write(json)
    }

    @Throws(IOException::class, RuntimeException::class)
    fun get(name: String, owner: String): CraftPlateGate? {
        val json = read()
        for (element in json) {
            val jo = element.asJsonObject
            if (jo["name"].asString == name && jo["owner"].asString == owner) {
                return CraftPlateGate(jo)
            }
        }
        return null
    }

    @Throws(IOException::class, RuntimeException::class)
    fun checkDuplicateName(name: String, owner: String): Boolean {
        return get(name, owner) != null
    }
}

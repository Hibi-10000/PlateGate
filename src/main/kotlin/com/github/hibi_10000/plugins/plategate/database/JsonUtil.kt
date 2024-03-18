/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.database.DBUtil.*
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.bukkit.block.BlockFace
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class JsonUtil(private val gateDB: File): DBUtil {
    class Entry(
        val id: Int,
        var owner: String,
        var name: String,
        var world: String,
        var x: Int,
        var y: Int,
        var z: Int,
        var rotate: String,
        var beforeBlock: String,
        var toOwner: String?,
        var toName: String?
    )

    /**
     * Read [List]<[Entry]> in [gateDB]
     * @return [MutableList]<[Entry]> in File
     * @throws IOException [FileReader(gateDB, Charset)][FileReader] If [gateDB] isn't found or some reasons
     * @throws JsonIOException [Gson.fromJson(Reader, Type)][Gson.fromJson] If there was a problem reading from the [FileReader]
     * @throws JsonSyntaxException [Gson.fromJson(Reader, Type)][Gson.fromJson] If contents of [File] are not [List]<[Entry]>
     */
    @Throws(IOException::class, RuntimeException::class)
    private fun read(): MutableList<Entry> {
        val array = FileReader(gateDB, StandardCharsets.UTF_8).use { reader ->
            val type = object : TypeToken<MutableList<Entry>>() {}
            Gson().fromJson(reader, type) ?: mutableListOf()
        }
        return array
    }

    /**
     * Write [List]<[Entry]> to [gateDB]
     * @param json [List]<[Entry]> to write to [gateDB]
     * @throws IOException [FileWriter(gateDB, Charset)][FileWriter] If [gateDB] cannot be opened
     * @throws JsonIOException [Gson.toJson(json, Writer)][Gson.toJson] If there was a problem writing to the [FileWriter]
     */
    @Throws(IOException::class, RuntimeException::class)
    private fun write(json: List<Entry>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        FileWriter(gateDB, StandardCharsets.UTF_8, false).use { writer ->
            gson.toJson(json, writer)
        }
    }

    private fun getLastId(json: List<Entry>): Int {
        var lastId = 0
        for (entry in json) {
            val id = entry.id
            if (lastId < id) lastId = id
        }
        return lastId
    }

    @Throws(RuntimeException::class)
    private fun checkLoc(json: List<Entry>, plateGate: CraftPlateGate) {
        val bool = { entry: Entry, world: UUID, x: Int, y: Int, z: Int ->
            entry.world == world.toString()
                && entry.x == x
                && entry.y == y
                && entry.z == z
        }
        for (entry in json) {
            val ys = listOf(2, 1, 0, -1, -2)
            for (y in ys) {
                if (bool(entry, plateGate.world, plateGate.x, plateGate.y + y, plateGate.z)) {
                    throw GateLocationDuplicateException()
                }
            }
            val xs = listOf(1, 0, -1)
            val zs = listOf(1, 0, -1)
            for (x in xs) { for (z in zs) { for (y in ys) {
                if (x == 0 && z == 0) continue
                if (bool(entry, plateGate.world, plateGate.x + x, plateGate.y + y, plateGate.z + z)) {
                    if (x == plateGate.rotate.modX && z == plateGate.rotate.modZ) {
                        throw GateLocationDuplicateException()
                    }
                    val dRotate = BlockFace.valueOf(entry.rotate.uppercase(Locale.ROOT))
                    if (   dRotate != plateGate.rotate
                        && dRotate != plateGate.rotate.oppositeFace
                        && x == dRotate.oppositeFace.modX
                        && z == dRotate.oppositeFace.modZ
                    ) {
                        throw GateLocationDuplicateException()
                    }
                }
            } } }
        }
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
        checkLoc(json, plateGate)
        val entry = Entry(
            getLastId(json) + 1,
            plateGate.owner.toString(),
            plateGate.name,
            plateGate.world.toString(),
            plateGate.x,
            plateGate.y,
            plateGate.z,
            plateGate.rotate.name,
            plateGate.beforeBlock.key.toString(),
            plateGate.toOwner?.toString(),
            plateGate.toName
        )
        json.add(entry)
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
    private fun get(json: List<Entry>, owner: UUID, name: String): CraftPlateGate? {
        for (entry in json) {
            if (entry.name == name && entry.owner == owner.toString()) {
                return CraftPlateGate(entry)
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
        for (entry in json) {
            if (entry.world == world.toString() && entry.x == x && entry.y == y && entry.z == z) {
                return CraftPlateGate(entry)
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
        for (entry in json) {
            if (entry.owner == owner.toString()) {
                list.add(CraftPlateGate(entry))
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
     * @return List of [CraftPlateGate] linked
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun link(owner: UUID, name: String, toOwner: UUID, toName: String): List<CraftPlateGate> {
        val json = read()
        val toGate = get(json, toOwner, toName) ?: throw GateNotFoundException()
        json.forEachIndexed { index, entry ->
            if (entry.name == name && entry.owner == owner.toString()) {
                entry.toName = toName
                entry.toOwner = toOwner.toString()
                json[index] = entry
                write(json)
                return listOf(CraftPlateGate(entry), toGate)
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
        checkLoc(json, plateGate)
        json.forEachIndexed { index, entry ->
            if (entry.name == plateGate.name && entry.owner == plateGate.owner.toString()) {
                entry.world = plateGate.world.toString()
                entry.x = plateGate.x
                entry.y = plateGate.y
                entry.z = plateGate.z
                entry.rotate = plateGate.rotate.name
                entry.beforeBlock = plateGate.beforeBlock.key.toString()
                json[index] = entry
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
        json.forEachIndexed { index, entry ->
            if (entry.name == name && entry.owner == owner.toString()) {
                json.removeAt(index)
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
     * @return [CraftPlateGate] with the new name
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun rename(owner: UUID, name: String, newName: String): CraftPlateGate {
        val json = read()
        if (get(json, owner, newName) != null) {
            throw GateNameDuplicateException()
        }
        json.forEachIndexed { index, entry ->
            if (entry.name == name && entry.owner == owner.toString()) {
                entry.name = newName
                json[index] = entry
                json.forEachIndexed { index2, entry2 ->
                    if (entry2.toName == name && entry2.toOwner == owner.toString()) {
                        entry2.toName = newName
                        json[index2] = entry2
                    }
                }
                write(json)
                return CraftPlateGate(entry)
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
        json.forEachIndexed { index, entry ->
            if (entry.name == name && entry.owner == owner.toString()) {
                entry.owner = newOwner.toString()
                json[index] = entry
                json.forEachIndexed { index2, entry2 ->
                    if (entry2.toName == name && entry2.toOwner == owner.toString()) {
                        entry2.toOwner = newOwner.toString()
                        json[index2] = entry2
                    }
                }
                write(json)
                return
            }
        }
        throw GateNotFoundException()
    }

    /**
     * Unlink the gate from another gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to unlink
     * @throws IOException see [read] and [write]
     * @throws RuntimeException see [read] and [write]
     * @see read
     * @see write
     */
    @Throws(IOException::class, RuntimeException::class)
    override fun unlink(owner: UUID, name: String) {
        val json = read()
        json.forEachIndexed { index, entry ->
            if (entry.name == name && entry.owner == owner.toString()) {
                if (entry.toOwner == null || entry.toName == null) {
                    throw GateNotLinkedException()
                }
                entry.toName = null
                entry.toOwner = null
                json[index] = entry
                write(json)
                return
            }
        }
        throw GateNotFoundException()
    }
}

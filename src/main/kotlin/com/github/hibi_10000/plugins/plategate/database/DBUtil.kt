/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.jsonDB
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class DBUtil {
    fun getJson(id: String, key: String, sender: Player?): String? {
        return try {
            val value = jsonDB!!.get(id, key)
            if (value == null) sender?.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
            value
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun addJson(values: Array<String>, sender: Player?) {
        try {
            val keys = arrayOf(
                "name",
                "owner",
                "to",
                "x",
                "y",
                "z",
                "rotate",
                "world",
                "beforeBlock"
                //TODO: createDate, updateDate
            )
            val map = keys.associateBy({ it }, { values[keys.indexOf(it)] })
            jsonDB!!.add(map)
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun setJson(id: String, key: String, value: String, sender: Player?) {
        try {
            jsonDB!!.set(id, key, value)
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun removeJson(id: String, sender: Player?) {
        try {
            jsonDB!!.remove(id)
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun firstIndexJson(key: String, value: String, sender: Player?): String? {
        return try {
            val id = jsonDB!!.firstIndexOf(key, value)
            if (id == "-1") {
                sender?.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
                return null
            }
            id
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun lastIndexJson(key: String?, value: String?, sender: Player?): String? {
        return try {
            val id = jsonDB!!.lastIndexOf(key, value)
            if (id == "-1") {
                sender?.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
                return null
            }
            id
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun allIndexJson(key: String?, value: String?, sender: Player?): List<String> {
        return try {
            val idList = jsonDB!!.allIndexOf(key, value)
            if (idList.isEmpty()) {
                sender?.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
                return emptyList()
            }
            idList
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun clearJson(sender: Player?) {
        try {
            jsonDB!!.clear()
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun allIndexJson(loc: Location, p: Player?): String? {
        val xIndexList = allIndexJson("x", loc.blockX.toString(), p)
        //List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
        //List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
        //List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
        //String index = "0";
        for (xIndex in xIndexList) {
            //boolean x = util.getJson(xIndex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
            val y = getJson(xIndex, "y", p).equals(loc.blockY.toString(), ignoreCase = true)
            val z = getJson(xIndex, "z", p).equals(loc.blockZ.toString(), ignoreCase = true)
            val w = getJson(xIndex, "world", p).equals(loc.getWorld().toString(), ignoreCase = true)
            //if (y && z && w) index = xIndex;
            if (y && z && w) return xIndex
        }
        return null
    }

    fun gateExists(id: String?, name: String?, sender: Player?): Boolean {
        return try {
            if (id != null) {
                return if (id.equals("0", ignoreCase = true)) false else jsonDB!!.get(id, "name") != null
            } else if (name != null) {
                if (jsonDB!!.firstIndexOf("name", name).equals("0", ignoreCase = true)) {
                    sender?.sendMessage("§a[PlateGate] §cゲート名が間違っています")
                    return false
                }
                return true
            }
            false
        } catch (e: Exception) {
            sender?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            throw RuntimeException(e)
        }
    }

    fun isDuplicateName(name: String, sender: Player?): Boolean {
        val search = dbUtil.firstIndexJson("name", name, sender)
        if (search != null) {
            if (jsonDB!!.get(search, "name").equals(name)) {
                sender?.sendMessage("§a[PlateGate] §cその名前は使用されています。")
            } else sender?.sendMessage("§a[PlateGate] §cERROR!")
            return true
        }
        return false
    }

    fun gateLocation(id: String, sender: Player?): Location {
        val world = getJson(id, "world", sender)!!
        val x = getJson(id, "x", sender)!!
        val y = getJson(id, "y", sender)!!
        val z = getJson(id, "z", sender)!!
        val rotate = getJson(id, "rotate", sender)!!
        val yaw = util.convFacing2Yaw(rotate)
        return Location(
            Bukkit.getWorld(world),
            x.toDouble(), y.toDouble(), z.toDouble(),
            yaw, 0f
        ).clone()
    }

    fun underBlock(id: String, sender: Player?): Material {
        return Material.getMaterial(getJson(id, "beforeBlock", sender)!!)!!
    }
}

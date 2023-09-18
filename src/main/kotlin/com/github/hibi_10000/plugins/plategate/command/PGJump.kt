/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGJump {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!checkPermission(sender, "plategate.command.jump")) return false
        if (args.size != 2) return commandInvalid(sender, label)
        val p = sender as Player

        val index = dbUtil.firstIndexJson("name", args[1], sender)
        val rotate = dbUtil.getJson(index, "rotate", sender)
        val yaw = util.convFacing2Yaw(rotate)

        val toloc = Location(
            Bukkit.getServer().getWorld(dbUtil.getJson(index, "world", sender)),
            dbUtil.getJson(index, "x", sender).toInt() + 0.5,
            dbUtil.getJson(index, "y", sender).toInt().toDouble(),
            dbUtil.getJson(index, "z", sender).toInt() + 0.5,
            yaw, 0f
        )
        when (rotate.lowercase()) {
            "north" -> toloc.z -= 1
            "east"  -> toloc.x += 1
            "south" -> toloc.z += 1
            "west"  -> toloc.x -= 1
        }
        val touploc = toloc.clone()
        touploc.y = toloc.y + 1
        touploc.block.type = Material.AIR
        toloc.block.type = Material.AIR
        p.teleport(toloc)
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        //List<String> list = new ArrayList<>();
        return null
    }
}

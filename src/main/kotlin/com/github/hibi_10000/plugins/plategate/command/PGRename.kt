/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGRename {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.rename")) return false
        if (args.size != 3) return util.commandInvalid(sender, label)

        if (dbUtil.isDuplicateName(args[2], sender as Player)) return false
        val index = dbUtil.firstIndexJson("name", args[1], sender) ?: return false
        val owner = util.getOfflinePlayer(dbUtil.getJson(index, "owner", sender)!!, null)!!
        if (owner.uniqueId.toString() != sender.uniqueId.toString()) {
            sender.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。")
            return false
        }
        dbUtil.setJson(index, "name", args[2], sender)
        sender.sendMessage("§a[PlateGate] §bPlateGate ${args[1]} を ${args[2]} にリネームしました")
        println("§a[PlateGate] §b${sender.name} がPlateGate ${args[1]} を ${args[2]} にリネームしました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGList {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.list")) return false
        if (!(args.size == 2 || args.size == 1)) return util.commandInvalid(sender, label)

        var searchP: Player = sender as Player
        if (args.size == 2) {
            if (!util.checkPermission(sender, "plategate.admin")) return false
            var args1player: Player? = null
            for (p in Bukkit.getOnlinePlayers()) {
                if (p.name.equals(args[1], ignoreCase = true)) {
                    args1player = p
                    break
                }
            }
            for (p in Bukkit.getOfflinePlayers()) {
                if (args1player == null) break
                if (p.name.equals(args[1], ignoreCase = true)) {
                    args1player = p as Player
                    break
                }
            }
            if (args1player == null) {
                sender.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
                return false
            }
            searchP = args1player
        }

        sender.sendMessage("§a[PlateGate] §bPlayer §6${searchP.name} §bが所有しているGate一覧")
        for (index in dbUtil.allIndexJson("owner", searchP.uniqueId.toString(), sender)) {
            if (dbUtil.getJson(index, "to", sender).equals("", ignoreCase = true)) {
                sender.sendMessage(" §b${dbUtil.getJson(index, "name", sender)}")
            } else {
                /*
                if (dbUtil.getJson(dbUtil.firstIndexJson("name", dbUtil.getJson(index, "to", sender)!!, sender)!!, "to", sender)
                        .equals(dbUtil.getJson(index, "name", sender), ignoreCase = true)) {
                    sender.sendMessage(" §b${dbUtil.getJson(index, "name", sender)} §a<--> §b${dbUtil.getJson(index, "to", sender)}")
                    continue
                }
                */
                sender.sendMessage(" §b${dbUtil.getJson(index, "name", sender)} §a---> §b${dbUtil.getJson(index, "to", sender)}")
            }
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

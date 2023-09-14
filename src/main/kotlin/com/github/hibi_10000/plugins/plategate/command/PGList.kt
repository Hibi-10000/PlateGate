/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util.util
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGList {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!checkPermission(sender, "plategate.command.list")) return false
        if (!(args.size == 2 || args.size == 1)) return commandInvalid(sender, label)

        val searchP: Player?
        if (sender.hasPermission("plategate.admin") && args.size == 2) {
            var args1player = false
            for (p in Bukkit.getOnlinePlayers()) {
                if (p.name.equals(args[1], ignoreCase = true)) {
                    args1player = true
                    break
                }
            }
            for (p in Bukkit.getOfflinePlayers()) {
                if (p.name.equals(args[1], ignoreCase = true)) {
                    args1player = true
                    break
                }
            }
            if (!args1player) {
                sender.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
                return false
            }
            searchP = Bukkit.getPlayer(args[1])
        } else {
            searchP = sender as Player
        }

        //List<JsonObject> jolist = new JsonHandler(plugin).JsonRead(searchp, null);
        for (index in util.allIndexJson("owner", searchP!!.uniqueId.toString(), (sender as Player))) {
            sender.sendMessage("§a[PlateGate] §bPlayer §6" + searchP.name + " §bが所有しているGate一覧")
            if (util.getJson(index, "to", sender).equals("", ignoreCase = true)) {
                sender.sendMessage(" §b" + util.getJson(index, "name", sender))
                //} else if (new JsonHandler(plugin).JsonRead(jog.get("to").getAsString(), null).getAsJsonObject()
                //		.get("to").getAsString() == jog.get("name").getAsString()) {
                //	sender.sendMessage(" " + jog.get("name").getAsString() + " <--> " + jog.get("to").getAsString());
            } else {
                sender.sendMessage(
                    " §b" + util.getJson(index, "name", sender) + " §a---> §b" + util.getJson(
                        index, "to", sender
                    )
                )
            }
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}
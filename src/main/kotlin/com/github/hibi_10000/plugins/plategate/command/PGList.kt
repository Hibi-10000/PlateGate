/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.dbUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGList {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.list")) return false
        if (!(args.size == 2 || args.size == 1)) return Util.commandInvalid(sender, label)

        var searchP = sender
        if (args.size == 2) {
            if (!Util.checkPermission(sender, "plategate.admin")) return false
            searchP = Util.getPlayer(args[1], sender) ?: return false
        }

        sender.sendMessage("§a[PlateGate] §bPlayer §6${searchP.name} §bが所有しているGate一覧")
        val gateList: List<CraftPlateGate>
        try {
            gateList = dbUtil.getList(searchP.uniqueId)
        } catch (e: Exception) {
            sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return false
        }
        for (gate in gateList) {
            if (gate.toName == null || gate.toOwner == null) {
                sender.sendMessage(" §b${gate.name}")
            } else {
                var toName = gate.toName
                if (gate.toOwner != searchP.uniqueId) {
                    val toOwner = Util.getPlayer(gate.toOwner!!, sender)
                    toName += " (Owner: ${toOwner?.name})"
                }
                var arrow = "--->"
                try {
                    val toGate = dbUtil.get(gate.toOwner!!, gate.toName!!)
                    if (toGate.toName == gate.name && toGate.toOwner == gate.owner) {
                        arrow = "<-->"
                    }
                } catch (e: Exception) {
                    sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
                }
                sender.sendMessage(" §b${gate.name} §a${arrow} §b${toName}")
            }
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

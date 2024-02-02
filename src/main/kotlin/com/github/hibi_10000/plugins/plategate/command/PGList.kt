/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGList {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.list")) return false
        if (!(args.size == 2 || args.size == 1)) return util.commandInvalid(sender, label)

        var searchP = sender
        if (args.size == 2) {
            if (!util.checkPermission(sender, "plategate.admin")) return false
            searchP = util.getPlayer(args[1], sender) ?: return false
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
                    val toOwner = util.getPlayer(gate.toOwner!!, sender) ?: continue
                    toName += " (Owner: ${toOwner.name})"
                }
                sender.sendMessage(" §b${gate.name} §a---> §b${toName}")
            }
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

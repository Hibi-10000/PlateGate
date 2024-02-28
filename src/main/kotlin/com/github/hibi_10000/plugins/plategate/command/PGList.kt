/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.MessageUtil
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.dbUtil
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
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
        val gateList = try {
            dbUtil.getList(searchP.uniqueId)
        } catch (e: Exception) {
            MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        for (gate in gateList) {
            val gateInfo = MessageUtil.getGateInfo(gate, sender)
            gateInfo.color = ChatColor.AQUA.asBungee()
            if (gate.toOwner == null || gate.toName == null) {
                sender.spigot().sendMessage(TextComponent(" "), gateInfo)
            } else {
                val toGate = try {
                    dbUtil.get(gate.toOwner!!, gate.toName!!)
                } catch (e: Exception) {
                    MessageUtil.catchUnexpectedError(sender, e)
                    sender.spigot().sendMessage(TextComponent(" "), gateInfo)
                    continue
                }
                val arrow = TextComponent("--->")
                if (toGate.toName == gate.name && toGate.toOwner == gate.owner) {
                    arrow.text = "<-->"
                }
                val toGateInfo = MessageUtil.getGateInfo(toGate, sender)
                if (gate.toOwner != searchP.uniqueId) {
                    val toOwner = Util.getPlayer(gate.toOwner!!, sender)
                    toGateInfo.text += " (Owner: ${toOwner?.name})"
                }
                arrow.color = ChatColor.GREEN.asBungee()
                toGateInfo.color = ChatColor.AQUA.asBungee()
                sender.spigot().sendMessage(
                    TextComponent(" "), gateInfo,
                    TextComponent(" "), arrow,
                    TextComponent(" "), toGateInfo
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

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.MessageUtil
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.localization.Message
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGList {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.list")) return false
        if (args.size != 1) return Util.commandInvalid(sender, label)

        val senderInfo = MessageUtil.getSenderInfo(sender)
        senderInfo.text = " §6${senderInfo.text} "
        MessageUtil.send(sender, Message.COMMAND_LIST_HEADER, senderInfo)
        val gateList = try {
            dbUtil.getList(sender.uniqueId)
        } catch (e: Exception) {
            MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        for (gate in gateList) {
            val gateInfo = MessageUtil.getGateInfo(gate, sender)
            gateInfo.text = " §b${gateInfo.text} "
            if (gate.toOwner == null || gate.toName == null) {
                sender.spigot().sendMessage(gateInfo)
            } else {
                val toGate = try {
                    dbUtil.get(gate.toOwner!!, gate.toName!!)
                } catch (e: Exception) {
                    MessageUtil.catchUnexpectedError(sender, e)
                    sender.spigot().sendMessage(gateInfo)
                    continue
                }
                val arrow = TextComponent("§a--->")
                if (toGate.toName == gate.name && toGate.toOwner == gate.owner) {
                    arrow.text = "§a<-->"
                }
                val toGateInfo = MessageUtil.getGateInfo(toGate, sender)
                toGateInfo.text = " §b${toGateInfo.text} "
                if (gate.toOwner != sender.uniqueId) {
                    val toOwner = Util.getPlayer(gate.toOwner!!, sender)
                    toGateInfo.text += "(Owner: ${toOwner?.name})"
                }
                sender.spigot().sendMessage(gateInfo, arrow, toGateInfo)
            }
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

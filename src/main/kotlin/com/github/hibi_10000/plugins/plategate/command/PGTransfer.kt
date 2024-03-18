/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.MessageUtil
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.localization.Message
import com.github.hibi_10000.plugins.plategate.transfer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object PGTransfer {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3) return Util.commandInvalid(sender, label)

        val gateName = args[1]
        when (args[2].lowercase(Locale.ROOT)) {
            "accept" -> {
                if (args.size != 3) return Util.commandInvalid(sender, label)
                //新しい所有者か確認
                val gate = transfer[sender.uniqueId]
                if (gate?.name != gateName) return false
                //TODO: /plategate transfer <name> accept 受け入れたときの処理
                return true
            }
            "reject" -> {
                if (args.size != 3) return Util.commandInvalid(sender, label)
                //新しい所有者か確認
                val gate = transfer[sender.uniqueId]
                if (gate?.name != gateName) return false
                //TODO: /plategate transfer <name> reject 拒否したときの処理
                return false
            }
            "cancel" -> {
                if (args.size != 3) return Util.commandInvalid(sender, label)
                val entry = transfer.entries.find { it.value.name == gateName && it.value.owner == sender.uniqueId }
                if (entry == null) {
                    MessageUtil.send(sender, Message.COMMAND_TRANSFER_CANCEL_ERROR_NOT_FOUND)
                    return false
                }
                val gate = entry.value
                val np = Util.getPlayer(entry.key, sender) ?: return false
                transfer.remove(np.uniqueId)
                val gateInfo = MessageUtil.getGateInfo(gate, sender)
                MessageUtil.sendWithLog(sender, Message.COMMAND_TRANSFER_CANCEL_SUCCESS, np, gateInfo)
                MessageUtil.send(np, Message.COMMAND_TRANSFER_CANCEL_SUCCESS_NOTICE, gateInfo)
                return true
            }
            "owner" -> {
                if (!Util.checkPermission(sender, "plategate.command.transfer")) return false
                if (args.size != 4) return Util.commandInvalid(sender, label)

                val gate = try {
                    dbUtil.get(sender.uniqueId, gateName)
                } catch (e: Exception) {
                    when (e) {
                        is DBUtil.GateNotFoundException -> MessageUtil.send(sender, Message.ERROR_GATE_NOT_FOUND)
                        else -> MessageUtil.catchUnexpectedError(sender, e)
                    }
                    return false
                }
                val newOwner = Util.getPlayer(args[3], sender) ?: return false
                transfer[newOwner.uniqueId] = gate

                val gateInfo = MessageUtil.getGateInfo(gate, sender)
                MessageUtil.sendWithLog(sender, Message.COMMAND_TRANSFER_REQUEST_SUCCESS, newOwner, gateInfo)
                val cancel = TextComponent("§c[キャンセルする]")
                cancel.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§cクリックで譲渡をキャンセルする"))
                cancel.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} cancel")
                sender.spigot().sendMessage(TextComponent("          "), cancel)

                MessageUtil.send(newOwner, Message.COMMAND_TRANSFER_REQUEST_SUCCESS_NOTICE, sender, gateInfo)
                val accept = TextComponent("§a[受け入れる]")
                accept.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで要求を受け入れる"))
                accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} accept")
                val reject = TextComponent("§c[拒否する]")
                reject.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§cクリックで要求を拒否する"))
                reject.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} reject")
                newOwner.spigot().sendMessage(TextComponent("            "), accept, TextComponent(" | "), reject)
                return true
            }
            else -> return Util.commandInvalid(sender, label)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        if (args.size <= 2) return null
        if (args.size == 3) {
            return listOf("owner")
        } else if (args[2].lowercase(Locale.ROOT) == "owner") {
            return Bukkit.getOnlinePlayers().map { it.name }
        }
        return null
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.localization.Message
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Entity
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.logging.Level

object MessageUtil {
    private fun send(receiver: CommandSender?, color: ChatColor?, message: Message, vararg format: String) {
        receiver?.sendMessage("${ChatColor.GREEN}[PlateGate] $color${message.getString(receiver, *format)}")
    }

    fun send(receiver: CommandSender?, message: Message, vararg format: String) {
        send(receiver, ChatColor.AQUA, message, *format)
    }

    fun sendError(receiver: CommandSender?, message: Message, vararg format: String) {
        send(receiver, ChatColor.RED, message, *format)
    }

    fun send(receiver: CommandSender?, message: Message, vararg format: BaseComponent) {
        val list = mutableListOf<BaseComponent>(TextComponent("§a[PlateGate] "))
        message.getString(receiver).split("%s").forEachIndexed { index, s ->
            list.add(TextComponent(s))
            if (index < format.size) list.add(format[index])
        }
        receiver?.spigot()?.sendMessage(*list.toTypedArray())
    }

    fun logInfo(message: Message, vararg format: String) {
        instance.logger.info(message.getString(*format))
    }

    fun catchUnexpectedError(sender: Player?, throwable: Throwable) {
        sendError(sender, Message.ERROR_UNEXPECTED)
        instance.logger.log(Level.SEVERE, Message.ERROR_UNEXPECTED.getString(), throwable)
    }

    fun sendActionBarError(receiver: Player?, message: Message, vararg format: String) {
        val component = TextComponent(message.getString(receiver, *format))
        component.color = ChatColor.RED.asBungee()
        receiver?.spigot()?.sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    fun getSenderInfo(sender: CommandSender): TextComponent {
        return TextComponent(sender.name).also {
            if (sender is org.bukkit.entity.Entity) {
                it.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_ENTITY, Entity(
                        sender.type.key.toString(), sender.uniqueId.toString(), TextComponent(sender.name)
                    )
                )
            } else if (sender is ConsoleCommandSender) {
                it.text = "Server"
            }
        }
    }

    fun getGateInfo(gate: CraftPlateGate, sender: CommandSender?): TextComponent {
        val owner = gate.owner.let { Util.getOfflinePlayer(it, sender) }
        val world = gate.getWorld()
        if (world == null) sendError(sender, Message.ERROR_WORLD_NOT_FOUND)
        val toOwner = gate.toOwner?.let { Util.getOfflinePlayer(it, sender) }
        val component = TextComponent(gate.name)
        component.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT, Text(
                "Name: ${gate.name
                }\nOwner: ${owner?.name
                }\nWorld: ${world?.name
                }\nX: ${gate.x
                }\nY: ${gate.y
                }\nZ: ${gate.z
                }\nRotate: ${gate.rotate.name
                }\nTo: ${gate.toName
                } (Owner: ${toOwner?.name
                })"
            )
        )
        return component
    }
}

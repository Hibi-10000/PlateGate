/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.localization.Message
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.TranslatableComponent
import net.md_5.bungee.api.chat.hover.content.Entity
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.logging.Level

object MessageUtil {
    private fun getMessage(receiver: CommandSender?, message: Message, isColored: Boolean, vararg format: Any): TranslatableComponent {
        return TranslatableComponent("plategate.bungee_components.null", *format).also {
            it.fallback = message.getString(receiver)
            if (isColored && message.color != null) it.color = message.color
        }
    }

    fun send(receiver: CommandSender?, message: Message, vararg format: Any) {
        receiver?.spigot()?.sendMessage(
            TextComponent("[PlateGate] ").also { it.color = ChatColor.GREEN },
            getMessage(receiver, message, true, *format)
        )
    }

    fun logInfo(message: Message, vararg format: String) {
        instance.server.consoleSender.spigot().sendMessage(getMessage(null, message, false, *format))
    }

    fun catchUnexpectedError(sender: Player?, throwable: Throwable) {
        send(sender, Message.ERROR_UNEXPECTED)
        instance.logger.log(Level.SEVERE, Message.ERROR_UNEXPECTED.getString(), throwable)
    }

    fun sendActionBarError(receiver: Player?, message: Message, vararg format: String) {
        receiver?.spigot()?.sendMessage(ChatMessageType.ACTION_BAR, getMessage(receiver, message, true, *format))
    }

    fun getSenderInfo(sender: CommandSender): TextComponent {
        return TextComponent(sender.name).also {
            if (sender is org.bukkit.entity.Entity) {
                it.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_ENTITY, Entity(
                        sender.type.key.toString(), sender.uniqueId.toString(), TextComponent(sender.name)
                    )
                )
                if (sender is Player) {
                    it.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell ${sender.name} ")
                }
            } else if (sender is ConsoleCommandSender) {
                it.text = "Server"
            }
        }
    }

    fun getGateInfo(gate: CraftPlateGate, sender: CommandSender?): TextComponent {
        val owner = gate.owner.let { Util.getOfflinePlayer(it, sender) }
        val world = gate.getWorld()
        if (world == null) send(sender, Message.ERROR_WORLD_NOT_FOUND)
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

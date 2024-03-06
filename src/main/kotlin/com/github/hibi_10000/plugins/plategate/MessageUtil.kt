/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.localization.Message
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
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
    fun send(receiver: CommandSender?, message: Message, vararg format: Any) {
        receiver?.spigot()?.sendMessage(
            TextComponent("[PlateGate] ").also { it.color = ChatColor.GREEN },
            TranslatableComponent("plategate.bungee_components.null", *format).also {
                it.fallback = message.getString(receiver)
                if (message.color != null) it.color = message.color
            }
        )
    }

    fun logInfo(message: Message, vararg format: String) {
        instance.logger.info(message.getString(*format))
    }

    fun catchUnexpectedError(sender: Player?, throwable: Throwable) {
        this.send(sender, Message.ERROR_UNEXPECTED)
        instance.logger.log(Level.SEVERE, Message.ERROR_UNEXPECTED.getString(), throwable)
    }

    fun sendActionBarError(receiver: Player?, message: Message, vararg format: String) {
        val component = TextComponent(message.getString(receiver, *format))
        component.color = ChatColor.RED
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
        if (world == null) this.send(sender, Message.ERROR_WORLD_NOT_FOUND)
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

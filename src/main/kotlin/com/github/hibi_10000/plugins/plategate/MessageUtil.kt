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
        return TranslatableComponent("plategate.bungee_components.null",
            *format.map { f ->
                when (f) {
                    is CraftPlateGate -> getGateInfo(f, receiver)
                    else -> f
                }
            }.toTypedArray()
        ).apply {
            fallback = message.getString(receiver)
            if (isColored && message.color != null) color = message.color
        }
    }

    fun send(receiver: CommandSender?, message: Message, vararg format: Any) {
        receiver?.spigot()?.sendMessage(
            TextComponent("[PlateGate] ").apply { color = ChatColor.GREEN },
            getMessage(receiver, message, true, *format)
        )
    }

    fun sendWithLog(receiver: CommandSender?, message: Message, vararg format: Any) {
        send(receiver, message, *format)
        logAdmin(receiver, message, *format)
    }

    private fun logAdmin(sender: CommandSender?, message: Message, vararg format: Any) {
        if (sender == null) return
        val messageComponent = getMessage(null, message, false, *format)
        val component = TranslatableComponent("chat.type.admin", getSenderInfo(sender), messageComponent).apply {
            color = ChatColor.GRAY
            isItalic = true
        }
        instance.server.consoleSender.spigot().sendMessage(component)
        instance.server.onlinePlayers.forEach { receiver ->
            if (receiver.uniqueId != (sender as Player).uniqueId && receiver.hasPermission("minecraft.admin.command_feedback")) {
                (component.with[1] as TranslatableComponent).fallback = message.getString(receiver)
                receiver.spigot().sendMessage(component)
            }
        }
    }

    fun catchUnexpectedError(sender: Player?, throwable: Throwable) {
        send(sender, Message.ERROR_UNEXPECTED)
        instance.logger.log(Level.SEVERE, Message.ERROR_UNEXPECTED.getString(), throwable)
    }

    fun sendActionBarError(receiver: Player?, message: Message, vararg format: String) {
        receiver?.spigot()?.sendMessage(ChatMessageType.ACTION_BAR, getMessage(receiver, message, true, *format))
    }

    fun getSenderInfo(sender: CommandSender): TextComponent {
        return TextComponent(sender.name).apply {
            if (sender is org.bukkit.entity.Entity) {
                hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_ENTITY, Entity(
                        sender.type.key.toString(), sender.uniqueId.toString(), TextComponent(sender.name)
                    )
                )
                if (sender is Player) {
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell ${sender.name} ")
                }
            } else if (sender is ConsoleCommandSender) {
                text = "Server"
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
                "§bName: §r${gate.name
                }\n§bOwner: §r${owner?.name
                }\n§bWorld: §r${world?.name
                }\n§bX: §r${gate.x
                }\n§bY: §r${gate.y
                }\n§bZ: §r${gate.z
                }\n§bRotate: §r${gate.rotate.name
                }\n§bTo: §r${gate.toName
                } §b(Owner: §r${toOwner?.name
                }§b)"
            )
        )
        return component
    }
}

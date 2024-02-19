/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.localization.Message
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Entity
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.logging.Level

object MessageUtil {
    fun sendMessage(sender: CommandSender?, color: ChatColor?, message: String) {
        sender?.sendMessage("${ChatColor.GREEN}[PlateGate] $color$message")
    }

    fun sendMessage(sender: CommandSender?, message: String) {
        sendMessage(sender, ChatColor.AQUA, message)
    }

    fun sendErrorMessage(sender: CommandSender?, message: String) {
        sendMessage(sender, ChatColor.RED, message)
    }

    fun logInfo(message: String) {
        instance.logger.info(message)
    }

    private fun send(receiver: Player, color: ChatColor?, message: Message, vararg format: String) {
        receiver.sendMessage("${ChatColor.GREEN}[PlateGate] $color${message.getString(receiver, *format)}")
    }

    fun send(receiver: Player, message: Message, vararg format: String) {
        send(receiver, ChatColor.AQUA, message, *format)
    }

    fun sendError(receiver: Player, message: Message, vararg format: String) {
        send(receiver, ChatColor.RED, message, *format)
    }

    fun logInfo(message: Message, vararg format: String) {
        logInfo(message.getString(*format))
    }

    fun catchUnexpectedError(sender: CommandSender?, throwable: Throwable) {
        sendErrorMessage(sender, "予期せぬエラーが発生しました")
        instance.logger.log(Level.SEVERE, "予期せぬエラーが発生しました", throwable)
    }

    fun sendActionBarErrorMessage(player: Player, message: String) {
        val component = TextComponent(message)
        component.color = ChatColor.RED.asBungee()
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component)
    }

    fun getPlayerInfo(player: Player): TextComponent {
        val component = TextComponent(player.name)
        component.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_ENTITY, Entity(
                "minecraft:player", player.uniqueId.toString(), TextComponent(player.name)
            )
        )
        return component
    }
}

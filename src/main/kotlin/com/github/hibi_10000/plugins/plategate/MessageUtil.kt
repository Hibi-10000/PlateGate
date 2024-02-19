/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.google.gson.Gson
import com.google.gson.JsonObject
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

    private fun format(base: String, vararg format: String): String {
        return if (format.isEmpty()) base else base.format(*format)
    }

    enum class Message(val jsonKey: String) {
        COMMANDS_CREATE_SUCCESS("commands.create.success"),
        COMMANDS_CREATE_SUCCESS_LOG("commands.create.success.log");

        private fun getMessage(vararg format: String): String {
            return format(Lang.getMessage(this), *format)
        }

        private fun getMessage(player: Player, vararg format: String): String {
            return format(Lang.fromString(player.locale).getMessage(this), *format)
        }

        private fun send(receiver: Player, color: ChatColor?, vararg format: String) {
            receiver.sendMessage("${ChatColor.GREEN}[PlateGate] $color${getMessage(receiver, *format)}")
        }

        fun send(receiver: Player, vararg format: String) {
            send(receiver, ChatColor.AQUA, *format)
        }

        fun sendError(receiver: Player, vararg format: String) {
            send(receiver, ChatColor.RED, *format)
        }

        fun logInfo(vararg format: String) {
            logInfo(getMessage(*format))
        }
    }

    private enum class Lang(private val key: String) {
        EN_US("en_us"),
        JA_JP("ja_jp");

        private val jo: JsonObject

        init {
            val json = instance.getResource("lang/${this.key}.json")?.use {
                it.readAllBytes().decodeToString()
            }
            jo = Gson().fromJson(json, JsonObject::class.java)
        }

        private val message = { key: Message -> jo[key.jsonKey]?.asString }

        fun getMessage(key: Message): String {
            return message(key) ?: Lang.getMessage(key)
        }

        companion object {
            fun fromString(lang: String): Lang {
                return entries.firstOrNull { it.key == lang } ?: EN_US
            }

            fun getMessage(key: Message): String {
                return EN_US.message(key) ?: JA_JP.message(key)!!
            }
        }
    }
}

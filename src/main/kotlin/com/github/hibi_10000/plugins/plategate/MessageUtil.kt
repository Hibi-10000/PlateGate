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

    enum class MessageKey(val jsonKey: String) {
        COMMANDS_CREATE_SUCCESS("commands.create.success"),
        COMMANDS_CREATE_SUCCESS_LOG("commands.create.success.log");

        fun getMessage(): String {
            return Lang.EN_US.getMessage(this) ?: Lang.JA_JP.getMessage(this)!!
        }

        private fun getMessage(lang: Lang): String {
            return lang.getMessage(this) ?: getMessage()
        }

        fun getMessage(sender: Player): String {
            return getMessage(Lang.get(sender.locale))
        }

        fun getMessage(vararg format: String): String {
            return getMessage().format(*format)
        }

        fun getMessage(sender: Player, vararg format: String): String {
            return getMessage(sender).format(*format)
        }
    }

    enum class Lang(val key: String) {
        EN_US("en_us"),
        JA_JP("ja_jp");

        private val jo: JsonObject

        init {
            val json = instance.getResource("lang/${this.key}.json")?.use {
                it.readAllBytes().decodeToString()
            }
            jo = Gson().fromJson(json, JsonObject::class.java)
        }

        fun getMessage(key: MessageKey): String? {
            return jo[key.jsonKey]?.asString
        }

        companion object {
            fun get(lang: String): Lang {
                return entries.firstOrNull { it.key == lang } ?: EN_US
            }
        }
    }
}

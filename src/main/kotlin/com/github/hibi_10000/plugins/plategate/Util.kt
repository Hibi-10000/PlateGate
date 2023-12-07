/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.regex.Pattern

class Util {
    fun checkPermission(sender: CommandSender, permission: String): Boolean {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage("§a[PlateGate] §c権限が不足しています。")
            return false
        }
        return true
    }

    fun commandInvalid(sender: CommandSender, label: String): Boolean {
        val help = TextComponent("§a[PlateGate] §cコマンドが間違っています。 /$label help で使用法を確認してください。")
        help.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで§b\"/$label help\"§aを実行"))
        help.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help")
        sender.spigot().sendMessage(help)
        return false
    }

    fun convBlockFace2Facing(blockFace: BlockFace): String {
        return when (blockFace) {
            BlockFace.SOUTH -> "south" /* yaw >= 315 || yaw <=  45 */
            BlockFace.WEST  -> "west"  /* yaw >   45 && yaw <  135 */
            BlockFace.NORTH -> "north" /* yaw >= 135 && yaw <= 225 */
            BlockFace.EAST  -> "east"  /* yaw >  225 && yaw <  315 */
            else            -> "south"
        }
    }

    fun convFacing2Yaw(facing: String): Float {
        return when (facing.lowercase()) {
            "south" ->   0f
            "west"  ->  90f
            "north" -> 180f
            "east"  -> 270f
            else    ->   0f
        }
    }

    fun underBlock(block: Block): Block {
        return block.getRelative(BlockFace.DOWN)
    }

    fun upperBlock(block: Block): Block {
        return block.getRelative(BlockFace.UP)
    }

    fun getPlayer(name: String, sender: Player?): Player? {
        val player = Bukkit.getPlayer(name)
        if (player == null) {
            if (Bukkit.getOfflinePlayers().find { it.name.equals(name, ignoreCase = true) } != null) {
                sender?.sendMessage("§a[PlateGate] §cそのプレイヤーはオフラインです。")
            } else {
                if (isUUID(name)) return getPlayer(UUID.fromString(name), sender)
                sender?.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
            }
        }
        return player
    }

    fun getPlayer(uuid: UUID, sender: Player?): Player? {
        val player = Bukkit.getPlayer(uuid)
        if (player == null) {
            if (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                sender?.sendMessage("§a[PlateGate] §cそのプレイヤーはオフラインです。")
            } else {
                sender?.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
            }
        }
        return player
    }

    fun getOfflinePlayer(name: String, sender: Player?): OfflinePlayer? {
        val player = Bukkit.getOfflinePlayers().find { it.name.equals(name, ignoreCase = true) }
        if (player == null) {
            if (isUUID(name)) return getOfflinePlayer(UUID.fromString(name), sender)
            sender?.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
            return null
        }
        return player
    }

    fun getOfflinePlayer(uuid: UUID, sender: Player?): OfflinePlayer? {
        val player = Bukkit.getOfflinePlayer(uuid)
        if (!player.hasPlayedBefore()) {
            sender?.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
            return null
        }
        return player
    }

    fun isUUID(string: String): Boolean {
        return Pattern.compile("^[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}$").matcher(string).find()
    }
}

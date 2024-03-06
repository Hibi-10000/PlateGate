/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.localization.Message
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.regex.Pattern

object Util {
    fun checkPermission(sender: CommandSender, permission: String): Boolean {
        if (!sender.hasPermission(permission)) {
            MessageUtil.send(sender, Message.ERROR_PERMISSION)
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

    fun firstUpperCase(str: String): String {
        return str.substring(0, 1).uppercase(Locale.ROOT) + str.substring(1).lowercase(Locale.ROOT)
    }

    fun underBlock(block: Block): Block {
        return block.getRelative(BlockFace.DOWN)
    }

    fun upperBlock(block: Block): Block {
        return block.getRelative(BlockFace.UP)
    }

    fun getBlockCenter(block: Block): Location {
        block.location.let { loc ->
            loc.x += 0.5
            loc.z += 0.5
            return loc
        }
    }

    fun convBlockFace2Yaw(blockFace: BlockFace): Float {
        return when (blockFace) {
            BlockFace.SOUTH ->   0f /* yaw >= 315 || yaw <=  45 */
            BlockFace.WEST  ->  90f /* yaw >   45 && yaw <  135 */
            BlockFace.NORTH -> 180f /* yaw >= 135 && yaw <= 225 */
            BlockFace.EAST  -> 270f /* yaw >  225 && yaw <  315 */
            else            ->   0f
        }
    }

    fun getPlayer(name: String, sender: CommandSender?): Player? {
        val player = Bukkit.getPlayer(name)
        if (player == null) {
            if (Bukkit.getOfflinePlayers().any { it.name?.lowercase(Locale.ROOT) == name }) {
                MessageUtil.send(sender, Message.ERROR_PLAYER_OFFLINE)
            } else {
                if (isUUID(name)) return getPlayer(UUID.fromString(name), sender)
                MessageUtil.send(sender, Message.ERROR_PLAYER_NOT_FOUND)
            }
        }
        return player
    }

    fun getPlayer(uuid: UUID, sender: CommandSender?): Player? {
        val player = Bukkit.getPlayer(uuid)
        if (player == null) {
            if (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                MessageUtil.send(sender, Message.ERROR_PLAYER_OFFLINE)
            } else {
                MessageUtil.send(sender, Message.ERROR_PLAYER_NOT_FOUND)
            }
        }
        return player
    }

    fun getOfflinePlayer(uuid: UUID, sender: CommandSender?): OfflinePlayer? {
        val player = Bukkit.getOfflinePlayer(uuid)
        if (!player.hasPlayedBefore()) {
            MessageUtil.send(sender, Message.ERROR_PLAYER_NOT_FOUND)
            return null
        }
        return player
    }

    private fun isUUID(string: String): Boolean {
        return Pattern.compile("^[0-9a-fA-F]{8}(-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}$").matcher(string).find()
    }

    fun noInteract(uuid: UUID) {
        noInteract.add(uuid)
        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, Runnable { noInteract.remove(uuid) }, 20L)
    }

    fun checkCreateLocation(sender: Player, loc: Location, underBlock: Block): Boolean {
        val message = when {
            sender.isFlying -> "地面に立っている必要があります"
            loc.y != loc.block.y.toDouble() -> "下のブロックはフルブロックである必要があります"
            loc.block.type != Material.AIR -> "その場所の非フルブロックを取り除いてください"
            underBlock.type.hasGravity() -> "下のブロックは重力の影響を受けないブロックである必要があります"
            underBlock.type.isInteractable
                || (underBlock.type.data != Material.AIR.data
                    && underBlock.type.data != Material.GRASS_BLOCK.data)
            -> "下のブロックはデータ値を持たないブロックである必要があります"
            !underBlock.type.isOccluding
                && !underBlock.type.isSolid
                && Material.entries.filter { it.name.contains("GLASS") }.none { it == underBlock.type }
            -> "下のブロックは非透過ブロックかガラスである必要があります"
            else -> return true
        }
        sender.sendMessage("§a[PlateGate] §c$message")
        return false
    }
}

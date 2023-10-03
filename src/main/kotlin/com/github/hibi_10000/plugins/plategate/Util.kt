package com.github.hibi_10000.plugins.plategate

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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

    fun underLocation(location: Location): Location {
        val underLocation = location.clone()
        underLocation.y -= 1
        return underLocation
    }

    fun upperLocation(location: Location): Location {
        val upperLocation = location.clone()
        upperLocation.y += 1
        return upperLocation
    }

    fun getPlayer(name: String, sender: Player?): Player? {
        var args1player: Player? = null
        for (p in Bukkit.getOnlinePlayers()) {
            if (p.name.equals(name, ignoreCase = true)) {
                args1player = p
                break
            }
        }
        for (p in Bukkit.getOfflinePlayers()) {
            if (args1player == null) break
            if (p.name.equals(name, ignoreCase = true)) {
                args1player = p.player
                break
            }
        }
        if (args1player == null) {
            sender?.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
        }
        return args1player
    }
}
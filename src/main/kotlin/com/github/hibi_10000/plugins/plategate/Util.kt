package com.github.hibi_10000.plugins.plategate

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender

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
}
package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGMove {
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("plategate.command.move")) {
            sender.sendMessage("§a[PlateGate] §c権限が不足しています。")
            return false
        }
        if (args.size != 2) {
            val help =
                TextComponent("§a[PlateGate] §cコマンドが間違っています。 /$label help で使用法を確認してください。")
            help.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text("§aクリックで§b\"/$label help\"§aを実行")
            )
            help.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help")
            sender.spigot().sendMessage(help)
            return false
        }
        if (!util.gateExists(null, args[1], (sender as Player))) return false
        val p = sender
        val ploc = p.location
        val loc = Location(ploc.getWorld(), ploc.x, ploc.y, ploc.z, ploc.yaw, 0f)
        val downloc = Location(p.world, loc.x, loc.y - 1, loc.z, loc.yaw, 0f)
        if (loc.block.type != Material.AIR) {
            sender.sendMessage("§a[PlateGate]§c その場所の非フルブロックを取り除いてください。")
            return false
        }
        if (loc.y != loc.blockY.toDouble()) {
            sender.sendMessage("§a[PlateGate]§c 下のブロックはフルブロックである必要があります。")
            return false
        }
        val downblockbefore = downloc.block.type
        downloc.block.type = Material.IRON_BLOCK
        loc.block.type = Material.STONE_PRESSURE_PLATE
        //Powerable ppbd = (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData();
        //ppbd.setPowered(false);
        //loc.getBlock().setBlockData(ppbd);


        //JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null);
        var index = util.firstIndexJson("name", args[1], sender)
        val oldloc = Location(
            Bukkit.getWorld(util.getJson(index, "world", sender)),
            util.getJson(
                index, "x", sender
            ).toInt().toDouble(),
            util.getJson(index, "y", sender).toInt().toDouble(),
            util.getJson(index, "z", sender).toInt()
                .toDouble()
        )
        val olddownloc =
            Location(p.world, oldloc.blockX.toDouble(), (oldloc.blockY - 1).toDouble(), oldloc.blockZ.toDouble())
        oldloc.block.type = Material.AIR
        olddownloc.block.type = Material.valueOf(util.getJson(index, "beforeblock", sender))

        //new JsonHandler(plugin).JsonChange(args[1], null, null, null, loc, downblockbefore, p);
        //float yaw = loc.getYaw();
        var d = "south"
        val pf = p.facing
        if ( /*(yaw >= 315 || yaw <= 45) ||  */pf == BlockFace.SOUTH) {
            d = "south"
        } else if ( /*(yaw > 45 && yaw < 135) || */pf == BlockFace.WEST) {
            d = "west"
        } else if ( /*(yaw >= 135 && yaw <= 225) || */pf == BlockFace.NORTH) {
            d = "north"
        } else if ( /*(yaw > 225 && yaw < 315) || */pf == BlockFace.EAST) {
            d = "east"
        }
        index = util.firstIndexJson("name", args[1], sender)
        util.setJson(index, "x", loc.blockX.toString(), sender)
        util.setJson(index, "y", loc.blockY.toString(), sender)
        util.setJson(index, "z", loc.blockZ.toString(), sender)
        util.setJson(index, "rotate", d, sender)
        util.setJson(index, "world", p.world.name, sender)
        util.setJson(index, "beforeblock", downblockbefore.toString(), sender)
        sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " を " + loc + " に移動しました")
        println("§a[PlateGate] §b" + p.name + " がゲート " + args[1] + " を " + loc + " に移動しました")
        return true
    }

    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

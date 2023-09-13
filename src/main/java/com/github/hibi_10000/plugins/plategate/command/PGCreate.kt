package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGCreate {
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("plategate.create")) {
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
        if (!util.getJson(util.firstIndexJson("name", args[1], (sender as Player)), "name", sender)
                .equals("0", ignoreCase = true)
        ) {
            //if (!(new JsonHandler(plugin).JsonRead(args[1], null).get("name").getAsString().equalsIgnoreCase("null"))) {
            if (util.getJson(util.firstIndexJson("name", args[1], sender), "name", sender).equals(
                    args[1], ignoreCase = true
                )
            ) {
                //if (new JsonHandler(plugin).JsonRead(args[1], null).get("name").getAsString().equalsIgnoreCase(args[1])) {
                sender.sendMessage("§a[PlateGate] §cその名前は使用されています。")
                return false
            }
            sender.sendMessage("§a[PlateGate] §cERROR!")
            return false
        }
        val p = sender
        val ploc = p.location
        val loc = Location(p.world, ploc.x, ploc.y, ploc.z)
        val downloc = Location(p.world, ploc.x, ploc.y - 1, ploc.z)
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
        //ppbd.setPowered(true);
        //uploc.getBlock().setBlockData(ppbd);


        //new JsonHandler(plugin).JsonWrite(args[1], p, "", loc, downblockbefore);
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
        util.addJson(
            sender,
            args[1],
            p.name,
            "",
            loc.blockX.toString(),
            loc.blockY.toString(),
            loc.blockZ.toString(),
            d,
            loc.getWorld().toString(),
            downblockbefore.toString()
        )
        sender.sendMessage("§a[PlateGate] §bPlateGate " + args[1] + " を " + loc + " に作成しました")
        println("§a[PlateGate] §b" + p.name + " がPlateGate " + args[1] + " を " + loc + " に作成しました")
        return true
    }

    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        //List<String> list = new ArrayList<>();
        //list.removeAll(list);


        //list.removeAll(list);
        return null
    }
}
package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGJump {

    fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("plategate.jump")) {
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
        val p = sender as Player
        /*
		JsonObject gateto = new JsonHandler(plugin).JsonRead(args[1], null);

		float Yaw = 0;
		String rotate = gateto.get("rotate").getAsString();

		if (rotate.equalsIgnoreCase("north")) {
			Yaw = 180;
		} else if (rotate.equalsIgnoreCase("east")) {
			Yaw = 270;
		} else if (rotate.equalsIgnoreCase("south")) {
			Yaw = 0;
		} else if (rotate.equalsIgnoreCase("west")) {
			Yaw = 90;
		}

		Location toloc = new Location(Bukkit.getServer().getWorld(gateto.get("world").getAsString()),
				Integer.parseInt(gateto.get("x").getAsString()) + 0.5, Integer.parseInt(gateto.get("y").getAsString()),
				Integer.parseInt(gateto.get("z").getAsString()) + 0.5, Yaw, 0);

		if (rotate.equalsIgnoreCase("north")) {
			toloc.setZ(toloc.getZ() - 1);
		} else if (rotate.equalsIgnoreCase("east")) {
			toloc.setX(toloc.getX() + 1);
		} else if (rotate.equalsIgnoreCase("south")) {
			toloc.setZ(toloc.getZ() + 1);
		} else if (rotate.equalsIgnoreCase("west")) {
			toloc.setX(toloc.getX() - 1);
		}

		Location touploc = toloc.clone();
		touploc.setY(toloc.getY() + 1);
		touploc.getBlock().setType(Material.AIR);
		toloc.getBlock().setType(Material.AIR);

		p.teleport(toloc);
		 */
        val index = util.firstIndexJson("name", args[1], sender)
        var Yaw = 0f
        val rotate = util.getJson(index, "rotate", sender)
        if (rotate.equals("north", ignoreCase = true)) Yaw = 180f else if (rotate.equals(
                "east",
                ignoreCase = true
            )
        ) Yaw = 270f else if (rotate.equals("south", ignoreCase = true)) Yaw = 0f else if (rotate.equals(
                "west",
                ignoreCase = true
            )
        ) Yaw = 90f
        val toloc = Location(
            Bukkit.getServer().getWorld(util.getJson(index, "world", sender)),
            util.getJson(index, "x", sender).toInt() + 0.5, util.getJson(index, "y", sender).toInt()
                .toDouble(),
            util.getJson(index, "z", sender).toInt() + 0.5, Yaw, 0f
        )
        if (rotate.equals("north", ignoreCase = true)) toloc.z -= 1
        else if (rotate.equals("east",ignoreCase = true)) toloc.x += 1
        else if (rotate.equals("south", ignoreCase = true)) toloc.z += 1
        else if (rotate.equals("west", ignoreCase = true)) toloc.x -= 1
        val touploc = toloc.clone()
        touploc.y = toloc.y + 1
        touploc.block.type = Material.AIR
        toloc.block.type = Material.AIR
        p.teleport(toloc)
        return true
    }

    fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        //List<String> list = new ArrayList<>();
        return null
    }
}

package com.github.hibi_10000.plugins.plategate.command;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import com.github.hibi_10000.plugins.plategate.util.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PGJump {
	
	private final PlateGate plugin;
	private final Util util;
	public PGJump (PlateGate instance) {
		this.plugin = instance;
		this.util = new Util(instance);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender.hasPermission("plategate.jump"))) {
			sender.sendMessage("§a[PlateGate] §c権限が不足しています。");
			return false;
		}

		if (!(args.length == 2)) {
			TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
			help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
			help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
			sender.spigot().sendMessage(help);
			return false;
		}
		
		Player p = (Player) sender;
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
		String index = util.firstIndexJson("name", args[1], (Player) sender);

		float Yaw = 0;
		String rotate = util.getJson(index, "rotate", (Player) sender);

		if (rotate.equalsIgnoreCase("north")) Yaw = 180;
		else if (rotate.equalsIgnoreCase("east")) Yaw = 270;
		else if (rotate.equalsIgnoreCase("south")) Yaw = 0;
		else if (rotate.equalsIgnoreCase("west")) Yaw = 90;

		Location toloc = new Location(Bukkit.getServer().getWorld(util.getJson(index, "world", (Player) sender)),
				Integer.parseInt(util.getJson(index, "x", (Player) sender)) + 0.5, Integer.parseInt(util.getJson(index, "y", (Player) sender)),
				Integer.parseInt(util.getJson(index, "z", (Player) sender)) + 0.5, Yaw, 0);

		if (rotate.equalsIgnoreCase("north")) toloc.setZ(toloc.getZ() - 1);
		else if (rotate.equalsIgnoreCase("east")) toloc.setX(toloc.getX() + 1);
		else if (rotate.equalsIgnoreCase("south")) toloc.setZ(toloc.getZ() + 1);
		else if (rotate.equalsIgnoreCase("west")) toloc.setX(toloc.getX() - 1);

		Location touploc = toloc.clone();
		touploc.setY(toloc.getY() + 1);
		touploc.getBlock().setType(Material.AIR);
		toloc.getBlock().setType(Material.AIR);

		p.teleport(toloc);
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		//List<String> list = new ArrayList<>();


		return null;
	}
}

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

public class PGDelete {
	
	private final PlateGate plugin;
	private final Util util;
	public PGDelete(PlateGate instance) {
		this.plugin = instance;
		this.util = new Util(instance);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender.hasPermission("plategate.delete"))) {
			sender.sendMessage("§a[PlateGate] §c権限が不足しています。");
			return false;
		}

		if (!(args.length == 2 || (args.length == 3 && args[2].equalsIgnoreCase("force")))) {
			TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
			help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
			help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
			sender.spigot().sendMessage(help);
			return false;
		}
		
		Player p = (Player) sender;

		/*
		JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
		if (jo.get("name").getAsString().equalsIgnoreCase("null")) {}

		if (!(Bukkit.getPlayer(jo.get("owner").getAsString()) == p)) {
			if (!(p.hasPermission("plategate.admin"))) {
				p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。");
				return false;
			}
			if (args.length == 3) {
				if (!(args[2].equalsIgnoreCase("force"))) {
					p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。");
					p.sendMessage("                       §b強制的に削除する場合はコマンドの末尾に \" force\" を付けてください。");
					return false;
				}
			}
		}

		Location oldloc = new Location(Bukkit.getWorld(jo.get("world").getAsString()), Double.parseDouble(jo.get("x").getAsString()),
				Double.parseDouble(jo.get("y").getAsString()), Double.parseDouble(jo.get("z").getAsString()));
		Location olddownloc = new Location(p.getWorld(), oldloc.getBlockX(), oldloc.getBlockY() - 1, oldloc.getBlockZ());

		oldloc.getBlock().setType(Material.AIR);
		olddownloc.getBlock().setType(Material.getMaterial(jo.get("beforeblock").getAsString()));

		new JsonHandler(plugin).JsonRemove(args[1]);
		//plugin.arrayJson.remove(plugin.arrayJson.firstIndexOf("name", args[1]));
		if ((args[2].equalsIgnoreCase("force"))) {
			p.sendMessage("§a[PlateGate] §bGate:" + jo.get("name").getAsString() + "(Owner:" + jo.get("Owner").getAsString() + ")を強制的に削除しました。");
			System.out.println("§a[PlateGate] §b" + p.getName() + " がGate:" + jo.get("name").getAsString() + "(Owner:" + jo.get("Owner").getAsString() + ")を強制的に削除しました。");
		} else {
			p.sendMessage("§a[PlateGate] §bGate:" + jo.get("name").getAsString() + "を削除しました。");
			System.out.println("§a[PlateGate] §b" + p.getName() + " がGate:" + jo.get("name").getAsString() + "を削除しました。");
		}
		*/

		String index = util.firstIndexJson("name", args[1], (Player) sender);
		if (util.getJson(index, "name", (Player) sender).equalsIgnoreCase("null")) {}

		if (!(Bukkit.getPlayer(util.getJson(index, "owner", (Player) sender)) == p)) {
			if (!(p.hasPermission("plategate.admin"))) {
				p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。");
				return false;
			}
			if (args.length == 3) {
				if (!(args[2].equalsIgnoreCase("force"))) {
					p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。");
					p.sendMessage("                       §b強制的に削除する場合はコマンドの末尾に \" force\" を付けてください。");
					return false;
				}
			}
		}

		Location oldloc = new Location(Bukkit.getWorld(util.getJson(index, "world", (Player) sender)), Double.parseDouble(util.getJson(index, "x", (Player) sender)),
				Double.parseDouble(util.getJson(index, "y", (Player) sender)), Double.parseDouble(util.getJson(index, "z", (Player) sender)));
		Location olddownloc = new Location(p.getWorld(), oldloc.getBlockX(), oldloc.getBlockY() - 1, oldloc.getBlockZ());

		oldloc.getBlock().setType(Material.AIR);
		olddownloc.getBlock().setType(Material.getMaterial(util.getJson(index, "beforeblock", (Player) sender)));

		util.removeJson(util.firstIndexJson("name", args[1], (Player) sender), (Player) sender);
		if ((args[2].equalsIgnoreCase("force"))) {
			p.sendMessage("§a[PlateGate] §bGate:" + util.getJson(index, "name", (Player) sender) + "(Owner:" + util.getJson(index, "Owner", (Player) sender) + ")を強制的に削除しました。");
			System.out.println("§a[PlateGate] §b" + p.getName() + " がGate:" + util.getJson(index, "name", (Player) sender) + "(Owner:" + util.getJson(index, "Owner", (Player) sender) + ")を強制的に削除しました。");
		} else {
			p.sendMessage("§a[PlateGate] §bGate:" + util.getJson(index, "name", (Player) sender) + "を削除しました。");
			System.out.println("§a[PlateGate] §b" + p.getName() + " がGate:" + util.getJson(index, "name", (Player) sender) + "を削除しました。");
		}
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		return null;
	}
}

package com.github.hibi_10000.plugins.plategate.command;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import com.github.hibi_10000.plugins.plategate.util.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PGList {
	
	private final PlateGate plugin;
	private final Util util;
	public PGList(PlateGate instance) {
		this.plugin = instance;
		this.util = new Util(instance);
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender.hasPermission("plategate.list"))) {
			sender.sendMessage("§a[PlateGate] §c権限が不足しています。");
			return false;
		}

		if (!(args.length == 2 || args.length == 1)) {
			TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
			help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
			help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
			sender.spigot().sendMessage(help);
			return false;
		}

		Player searchp;
		if (sender.hasPermission("plategate.admin") && args.length == 2) {

			boolean args1player = false;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().equalsIgnoreCase(args[1])) {
					args1player = true;
					break;
				}
			}
			for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
				if (p.getName().equalsIgnoreCase(args[1])) {
					args1player = true;
					break;
				}
			}
			if (!args1player) {
				sender.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。");
				return false;
			}

			searchp = Bukkit.getPlayer(args[1]);
		} else {
			searchp = (Player) sender;
		}

		//List<JsonObject> jolist = new JsonHandler(plugin).JsonRead(searchp, null);

		for (String index : util.IndexJson("owner", searchp.getUniqueId().toString(), (Player) sender)) {
			sender.sendMessage("§a[PlateGate] §bPlayer §6" + searchp.getName() + " §bが所有しているGate一覧");
			if (util.getJson(index, "to", (Player) sender).equalsIgnoreCase("")) {
				sender.sendMessage(" §b" + util.getJson(index, "name", (Player) sender));
			//} else if (new JsonHandler(plugin).JsonRead(jog.get("to").getAsString(), null).getAsJsonObject()
			//		.get("to").getAsString() == jog.get("name").getAsString()) {
			//	sender.sendMessage(" " + jog.get("name").getAsString() + " <--> " + jog.get("to").getAsString());
			} else {
				sender.sendMessage(" §b" + util.getJson(index, "name", (Player) sender) + " §a---> §b" + util.getJson(index, "to", (Player) sender));
			}
		}


		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		return null;
	}
}

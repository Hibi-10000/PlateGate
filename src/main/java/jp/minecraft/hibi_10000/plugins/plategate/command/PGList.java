package jp.minecraft.hibi_10000.plugins.plategate.command;

import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import jp.minecraft.hibi_10000.plugins.plategate.JsonHandler;
import jp.minecraft.hibi_10000.plugins.plategate.PlateGate;

public class PGList {
	
	private PlateGate plugin;
	public PGList(PlateGate instance) {
		this.plugin = instance;
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
			if (args1player == false) {
				sender.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。");
				return false;
			}

			searchp = Bukkit.getPlayer(args[1]);
		} else {
			searchp = (Player) sender;
		}

		List<JsonObject> jolist = new JsonHandler(plugin).JsonRead(searchp, null);
		
		sender.sendMessage("§a[PlateGate] §bPlayer §6" + searchp.getName() + " §bが所有しているGate一覧");
		
		for (JsonObject jo : jolist) {
			JsonObject jog = jo.getAsJsonObject();
			if (jog.get("to").getAsString().equalsIgnoreCase("")) {
				sender.sendMessage(" §b" + jog.get("name").getAsString());
			//} else if (new JsonHandler(plugin).JsonRead(jog.get("to").getAsString(), null).getAsJsonObject()
			//		.get("to").getAsString() == jog.get("name").getAsString()) {
			//	sender.sendMessage(" " + jog.get("name").getAsString() + " <--> " + jog.get("to").getAsString());
			} else {
				sender.sendMessage(" §b" + jog.get("name").getAsString() + " §a---> §b" + jog.get("to").getAsString());
			}
		}
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		return null;
	}
}

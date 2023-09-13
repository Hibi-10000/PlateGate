package com.github.hibi_10000.plugins.plategate.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import com.github.hibi_10000.plugins.plategate.JsonHandler;
import com.github.hibi_10000.plugins.plategate.PlateGate;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class PGModify {
	private Player ttop;
	private String ttopgn;
	private Player oldowner;
	
	private PlateGate plugin;
	public PGModify (PlateGate instance) {
		this.plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender.hasPermission("plategate.create"))) {
			sender.sendMessage("§a[PlateGate] §c権限が不足しています。");
			return false;
		}

		if (!(args.length <= 2)) {
			TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
			help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
			help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
			sender.spigot().sendMessage(help);
			return false;
		}
		
		if (args[2].equalsIgnoreCase("name")) {
			
			if (args.length <= 3) {
				sender.sendMessage("");
				return false;
			}
			
			new JsonHandler(plugin).JsonChange(args[1], args[3], null, null, null, null, null);
			
			sender.sendMessage("[] " + args[1] + "" + args[3] + "");
			
			return true;
			
		} else if (args[2].equalsIgnoreCase("owner")) {
			
			if (args.length <= 3) {
				sender.sendMessage("");
				return false;
			}
			
			if (args.length > 5) {
				sender.sendMessage("");
				return false;
			}
			
			if (args.length == 5) {
				if (args[4].equalsIgnoreCase("force")) {
					
				JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
				if (jo.get("name").getAsString().equalsIgnoreCase("null")) {
					sender.sendMessage("");
					return false;
				}
				Player gateoldowner = Bukkit.getPlayer(UUID.fromString(jo.get("owner").getAsString()));
					
					for (Player lp : Bukkit.getOnlinePlayers()) {
						if (lp.getName().equalsIgnoreCase(args[3])) {
							
							sender.sendMessage("");
							gateoldowner.sendMessage(""+ args[1] + "" + sender.getName() + "" + args[3] + "");
							System.out.println("");
							
							new JsonHandler(plugin).JsonChange(args[1], null, Bukkit.getPlayer(args[3]), null, null, null, null);
							
							return true;
						}
					}
					sender.sendMessage("" + args[3] + "");
					return false;
				}
				sender.sendMessage("");
				return false;
			}
			
			if (!(Bukkit.getPlayer(UUID.fromString(new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject()
					.get("owner").getAsString())).getName().equalsIgnoreCase(sender.getName()))) {
				
				sender.sendMessage("");
				return false;
			}
			
			if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[3]))) {
				JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
				if (jo.get("name").getAsString().equalsIgnoreCase("null")) {
					sender.sendMessage("");
					return false;
				}
				if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(jo.get("owner").getAsString())))) {
					
					oldowner = (Player) sender;
					ttop = Bukkit.getPlayer(args[3]);
					ttopgn = args[1];
					
					ttop.sendMessage("");
					
					TextComponent tonewowner = new TextComponent("[] 受け入れる");
					tonewowner.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("クリックで要求を受け入れる")));
					tonewowner.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " modify " + ttopgn + " accept"));
					
					ttop.spigot().sendMessage(tonewowner);
					
					return true;
				}
			}
			
			sender.sendMessage("" + args[3] + "");
			
			return false;
			
		} else if (args[2].equalsIgnoreCase("accept")) {
			
			if (oldowner.getName().equalsIgnoreCase(sender.getName())) {
				
				
				
			}
		}
		
		return false;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		
		if (args.length <= 2) return list;
		
		if (args.length == 3) {
			list.add("name");
			list.add("owner");
			list.add("accept");
			return list;
		} else if (args[2].equalsIgnoreCase("owner")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (args[3].equalsIgnoreCase(p.getName()) && (p.isOp() || p.hasPermission("          "))) {
					list.clear();
					list.add("force");
					return list;
				}
				list.add(p.getName());
			}
			return list;
		}
		
		
		
		list.clear();
		return list;
	}
}

package com.github.hibi_10000.plugins.plategate.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class PGHelp {
	
	private PlateGate plugin;
	public PGHelp(PlateGate instance) {
		this.plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender.hasPermission("plategate.help"))) {
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
		
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("plugin")) {
				sender.sendMessage("§a[PlateGate " + plugin.ver + "] §6MoreHelp");
				sender.sendMessage("Version: §b" + plugin.ver);
				sender.sendMessage("Auther: Hibi_10000");
				sender.sendMessage("BukkitDev: https://dev.bukkit.org/projects/plategate");
				return true;
			}
			TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
			help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
			help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
			sender.spigot().sendMessage(help);
			return false;
		}
		
		
		
		sender.sendMessage("§a[PlateGate " + plugin.ver + "] §6Help");
		
		sender.sendMessage(" §6Command§r:");
		
		TextComponent help1 = new TextComponent(" - §b/" + label +" create [GateName]");
		help1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " create\"§aをチャットにセット")));
		help1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " create "));
		sender.spigot().sendMessage(help1);
		
		TextComponent help2 = new TextComponent(" - §b/" + label +" move [GateName]");
		help2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " move\"§aをチャットにセット")));
		help2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " move "));
		sender.spigot().sendMessage(help2);
		
		TextComponent help3 = new TextComponent(" - §b/" + label +" delete [GateName]");
		help3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " delete\"§aをチャットにセット")));
		help3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " delete "));
		sender.spigot().sendMessage(help3);
		
		TextComponent help4 = new TextComponent(" - §b/" + label +" link [GateName:ここから] [GateName:ここへ飛ぶ]");
		help4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " link\"§aをチャットにセット")));
		help4.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " link "));
		sender.spigot().sendMessage(help4);
		
		/*
		TextComponent help5 = new TextComponent(" - §b/" + label +" modify [TargetGateName] [Object:<name/owner(OPOnly)>] [値:<NewGateName/Player>]");
		help5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " modify\"§aをチャットにセット")));
		help5.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " modify "));
		sender.spigot().sendMessage(help5);
		*/

		TextComponent help6 = new TextComponent(" - §b/" + label +" list [Player(OPOnly)]");
		help6.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " list\"§aをチャットにセット")));
		help6.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " list "));
		sender.spigot().sendMessage(help6);
		
		TextComponent help7 = new TextComponent(" - §b/" + label +" help");
		help7.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
		help7.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
		sender.spigot().sendMessage(help7);
		
		TextComponent help8 = new TextComponent(" - §b/" + label +" jump [GateName]");
		help8.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " jump\"§aをチャットにセット")));
		help8.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " jump "));
		sender.spigot().sendMessage(help8);
		
		/*
		sender.sendMessage(" §6Permission:§r");
		
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		*/
		
		sender.sendMessage("");
		
		TextComponent morehelp = new TextComponent(" §aHelpPlugin");
		morehelp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help plugin\"§aを実行")));
		morehelp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help plugin"));
		sender.spigot().sendMessage(morehelp);
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		return null;
	}
}

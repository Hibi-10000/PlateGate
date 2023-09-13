package com.github.hibi_10000.plugins.plategate.command;

import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.hibi_10000.plugins.plategate.JsonHandler;
import com.github.hibi_10000.plugins.plategate.PlateGate;

public class PGLink {
	
	private PlateGate plugin;
	public PGLink(PlateGate instance) {
		this.plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender.hasPermission("plategate.link"))) {
			sender.sendMessage("§a[PlateGate] §c権限が不足しています。");
			return false;
		}

		if (!(args.length == 3)) {
			TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
			help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
			help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
			sender.spigot().sendMessage(help);
			return false;
		}
		
		new JsonHandler(plugin).JsonChange(args[1], null, null, args[2], null, null, null);
		
		sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " から ゲート " + args[2] + " の方向にゲートをリンクしました。");
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		
		return null;
	}
}

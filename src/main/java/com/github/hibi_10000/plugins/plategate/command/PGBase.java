package com.github.hibi_10000.plugins.plategate.command;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.github.hibi_10000.plugins.plategate.PlateGate;

public class PGBase implements CommandExecutor, TabCompleter {
	
	private final PlateGate plugin;
	public PGBase(PlateGate instance) {
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pg")) {

			if (!(sender.hasPermission("plategate.command"))) {
				sender.sendMessage("§a[PlateGate] §c権限が不足しています。");
				return false;
			}

			if ((args.length == 0)) {
				TextComponent help = new TextComponent("§a[PlateGate] §cコマンドが間違っています。 /" + label +" help で使用法を確認してください。");
				help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " help\"§aを実行")));
				help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " help"));
				sender.spigot().sendMessage(help);
				return false;
			}
			
			if (args[0].equalsIgnoreCase("create")) {
				return new PGCreate(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("move")) {
				return new PGMove(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("delete")) {
				return new PGDelete(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("link")) {
				return new PGLink(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("modify")) {
				//return new PGModify(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("list")) {
				return new PGList(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("help")) {
				return new PGHelp(plugin).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("jump")) {
				return new PGJump(plugin).onCommand(sender, cmd, label, args);
				
			//} else if (args[0].equalsIgnoreCase("test")) {
				//Player p = (Player) sender;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();
		
		if (args[0].equalsIgnoreCase("create")) {
			return new PGCreate(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("move")) {
			return new PGMove(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("delete")) {
			return new PGDelete(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("link")) {
			return new PGLink(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("modify")) {
			//new PGModify(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("list")) {
			return new PGList(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("help")) {
			return new PGHelp(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("jump")) {
			return new PGJump(plugin).onTabComplete(sender, command, alias, args);
			
		} else if (args.length == 1) {
			list.add("create");
			list.add("move");
			list.add("delete");
			list.add("link");
			//list.add("modify");
			list.add("list");
			list.add("help");
			list.add("jump");
			return list;
		}
		return null;
	}
	
}

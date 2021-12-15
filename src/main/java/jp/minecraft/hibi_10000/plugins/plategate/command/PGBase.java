package jp.minecraft.hibi_10000.plugins.plategate.command;

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

import jp.minecraft.hibi_10000.plugins.plategate.PlateGate;

public class PGBase implements CommandExecutor, TabCompleter {
	
	private PlateGate instance;
	public PGBase(PlateGate instance) {
		this.instance = instance;
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
				return new PGCreate(instance).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("move")) {
				return new PGMove(instance).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("delete")) {
				return new PGDelete(instance).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("link")) {
				return new PGLink(instance).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("modify")) {
				
				
			} else if (args[0].equalsIgnoreCase("list")) {
				return new PGList(instance).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("help")) {
				return new PGHelp(instance).onCommand(sender, cmd, label, args);
				
			} else if (args[0].equalsIgnoreCase("jump")) {
				return new PGJump(instance).onCommand(sender, cmd, label, args);
				
			//} else if (args[0].equalsIgnoreCase("test")) {
				//Player p = (Player) sender;
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		
		if (args[0].equalsIgnoreCase("create")) {
			return new PGCreate(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("move")) {
			return new PGMove(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("delete")) {
			return new PGDelete(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("link")) {
			return new PGLink(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("modify")) {
			
			
		} else if (args[0].equalsIgnoreCase("list")) {
			return new PGList(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("help")) {
			return new PGHelp(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args[0].equalsIgnoreCase("jump")) {
			return new PGJump(instance).onTabComplete(sender, command, alias, args);
			
		} else if (args.length == 1) {
			list.removeAll(list);
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
		
		
		list.removeAll(list);
		return list;
	}
	
}

package jp.minecraft.hibi_10000.plugins.plategate.command;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jp.minecraft.hibi_10000.plugins.plategate.JsonHandler;
import jp.minecraft.hibi_10000.plugins.plategate.PlateGate;
import org.bukkit.permissions.Permission;

public class PGCreate {
	
	private PlateGate instance;
	public PGCreate(PlateGate instance) {
		this.instance = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender.hasPermission("plategate.create"))) {
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
		
		if (!(new JsonHandler(instance).JsonRead(args[1], null).get("name").getAsString() == "null")) {
			
			if (new JsonHandler(instance).JsonRead(args[1], null).get("name").getAsString().equalsIgnoreCase(args[1])) {
				sender.sendMessage("§a[PlateGate] §cその名前は使用されています。");
				return false;
			}
			
			sender.sendMessage("§a[PlateGate] §cERROR!");
			return false;
		}
		
		
		
		Player p = (Player) sender;
		Location ploc = p.getLocation();
		
		Location loc = new Location(p.getWorld(), ploc.getX(), ploc.getY(), ploc.getZ());
		Location downloc = new Location(p.getWorld(), ploc.getX(), ploc.getY() - 1, ploc.getZ());
		Location uploc = loc;
		
		if (!(loc.getBlock().getType() == Material.AIR)) {
			sender.sendMessage("§a[PlateGate]§c その場所の非フルブロックを取り除いてください。");
			return false;
		}
		if (!(loc.getY() == loc.getBlockY())) {
			sender.sendMessage("§a[PlateGate]§c 下のブロックはフルブロックである必要があります。");
			return false;
		}
		
		Material downblockbefore = downloc.getBlock().getType();
		
		downloc.getBlock().setType(Material.IRON_BLOCK);
		uploc.getBlock().setType(Material.STONE_PRESSURE_PLATE);
		//Powerable ppbd = (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData();
		//ppbd.setPowered(true);
		//uploc.getBlock().setBlockData(ppbd);
		
		
		new JsonHandler(instance).JsonWrite(args[1], p, "", loc, downblockbefore);
		
		sender.sendMessage("§a[PlateGate] §bPlateGateを作成しました。");
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		//list.removeAll(list);
		
		
		//list.removeAll(list);
		return list;
	}

}

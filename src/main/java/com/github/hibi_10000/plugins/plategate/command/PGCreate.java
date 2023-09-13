package com.github.hibi_10000.plugins.plategate.command;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import com.github.hibi_10000.plugins.plategate.util.Util;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PGCreate {
	
	private final PlateGate plugin;
	private final Util util;
	public PGCreate(PlateGate instance) {
		this.plugin = instance;
		this.util = new Util(instance);
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

		if (!(util.getJson(util.firstIndexJson("name", args[1], (Player) sender), "name", (Player) sender).equalsIgnoreCase("0"))) {
		//if (!(new JsonHandler(plugin).JsonRead(args[1], null).get("name").getAsString().equalsIgnoreCase("null"))) {

			if (util.getJson(util.firstIndexJson("name", args[1], (Player) sender), "name", (Player) sender).equalsIgnoreCase(args[1])) {
			//if (new JsonHandler(plugin).JsonRead(args[1], null).get("name").getAsString().equalsIgnoreCase(args[1])) {
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
		loc.getBlock().setType(Material.STONE_PRESSURE_PLATE);
		//Powerable ppbd = (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData();
		//ppbd.setPowered(true);
		//uploc.getBlock().setBlockData(ppbd);
		
		
		//new JsonHandler(plugin).JsonWrite(args[1], p, "", loc, downblockbefore);
		//float yaw = loc.getYaw();
		String d = "south";
		BlockFace pf = p.getFacing();
		if (/*(yaw >= 315 || yaw <= 45) ||  */pf == BlockFace.SOUTH){
			d = "south";
		} else if (/*(yaw > 45 && yaw < 135) || */pf == BlockFace.WEST){
			d = "west";
		} else if (/*(yaw >= 135 && yaw <= 225) || */pf == BlockFace.NORTH){
			d = "north";
		} else if (/*(yaw > 225 && yaw < 315) || */pf == BlockFace.EAST){
			d = "east";
		}

		util.addJson((Player) sender, args[1], p.getName(), "", String.valueOf(loc.getBlockX()), String.valueOf(loc.getBlockY()), String.valueOf(loc.getBlockZ()), d, loc.getWorld().toString(), downblockbefore.toString());
		sender.sendMessage("§a[PlateGate] §bPlateGate " + args[1] + " を " + loc + " に作成しました");
		System.out.println("§a[PlateGate] §b" + p.getName() + " がPlateGate " + args[1] + " を " + loc + " に作成しました");

		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		//List<String> list = new ArrayList<>();
		//list.removeAll(list);
		
		
		//list.removeAll(list);
		return null;
	}

}

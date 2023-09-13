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
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PGMove {
	
	private final PlateGate plugin;
	private final Util util;
	public PGMove(PlateGate instance) {
		this.plugin = instance;
		this.util = new Util(instance);
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender.hasPermission("plategate.move"))) {
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

		if (!util.gateExists(null, args[1], (Player) sender)) return false;
		
		Player p = (Player) sender;
		Location ploc = p.getLocation();
		Location loc = new Location(ploc.getWorld(), ploc.getX(), ploc.getY(), ploc.getZ(), ploc.getYaw(), 0);
		Location downloc = new Location(p.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ(), loc.getYaw(), 0);
		
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
		//ppbd.setPowered(false);
		//loc.getBlock().setBlockData(ppbd);
		
		
		//JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null);
		String index = util.firstIndexJson("name", args[1], (Player) sender);
		
		Location oldloc = new Location(Bukkit.getWorld(util.getJson(index, "world", (Player) sender)), Integer.parseInt(util.getJson(index, "x", (Player) sender)),
				Integer.parseInt(util.getJson(index, "y", (Player) sender)), Integer.parseInt(util.getJson(index, "z", (Player) sender)));
		Location olddownloc = new Location(p.getWorld(), oldloc.getBlockX(), oldloc.getBlockY() - 1, oldloc.getBlockZ());
		
		oldloc.getBlock().setType(Material.AIR);
		olddownloc.getBlock().setType(Material.valueOf(util.getJson(index, "beforeblock", (Player) sender)));
		
		//new JsonHandler(plugin).JsonChange(args[1], null, null, null, loc, downblockbefore, p);
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

		index = util.firstIndexJson("name", args[1], (Player) sender);
		util.setJson(index, "x", String.valueOf(loc.getBlockX()), (Player) sender);
		util.setJson(index, "y", String.valueOf(loc.getBlockY()), (Player) sender);
		util.setJson(index, "z", String.valueOf(loc.getBlockZ()), (Player) sender);
		util.setJson(index, "rotate", d, (Player) sender);
		util.setJson(index, "world", p.getWorld().getName(), (Player) sender);
		util.setJson(index, "beforeblock", downblockbefore.toString(), (Player) sender);

		sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " を " + loc + " に移動しました");
		System.out.println("§a[PlateGate] §b" + p.getName() + " がゲート " + args[1] + " を " + loc + " に移動しました");

		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		return null;
	}
}

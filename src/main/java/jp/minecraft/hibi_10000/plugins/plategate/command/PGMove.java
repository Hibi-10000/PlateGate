package jp.minecraft.hibi_10000.plugins.plategate.command;

import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Powerable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import jp.minecraft.hibi_10000.plugins.plategate.JsonHandler;
import jp.minecraft.hibi_10000.plugins.plategate.PlateGate;

public class PGMove {
	
	private PlateGate instance;
	public PGMove(PlateGate instance) {
		this.instance = instance;
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
		
		if ((new JsonHandler(instance).JsonRead(args[1], null).get("name").getAsString() == "null")) {
			
			sender.sendMessage("[]");
			return false;
		}
		
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
		
		
		JsonObject jo = new JsonHandler(instance).JsonRead(args[1], null);
		
		Location oldloc = new Location(Bukkit.getWorld(jo.get("world").getAsString()), Integer.parseInt(jo.get("x").getAsString()), 
				Integer.parseInt(jo.get("y").getAsString()), Integer.parseInt(jo.get("z").getAsString()));
		Location olddownloc = new Location(p.getWorld(), oldloc.getBlockX(), oldloc.getBlockY() - 1, oldloc.getBlockZ());
		
		oldloc.getBlock().setType(Material.AIR);
		olddownloc.getBlock().setType(Material.getMaterial(jo.get("beforeblock").getAsString()));
		
		new JsonHandler(instance).JsonChange(args[1], null, null, null, loc, downblockbefore, p);
		
		sender.sendMessage("§a[PlateGate] §b" + args[1] + " を正常に移動しました。");
		
		return true;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		return null;
	}
}

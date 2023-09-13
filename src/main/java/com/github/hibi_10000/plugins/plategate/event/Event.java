package com.github.hibi_10000.plugins.plategate.event;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import com.github.hibi_10000.plugins.plategate.util.Util;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.UUID;

public class Event implements Listener {
	
	private final PlateGate plugin;
	private final Util util;
	public Event(PlateGate instance) {
		this.plugin = instance;
		this.util = new Util(instance);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		//if (!plugin.isEnabled()) return;
		//if (e.useInteractedBlock() == e.useInteractedBlock().DENY
		//		|| e.useItemInHand() == e.useItemInHand().DENY) return;
		//Powerable spp = (Powerable) e.getClickedBlock().getBlockData();
		//if ((spp.isPowered())) {
			//e.getPlayer().sendMessage("");
		//	return;
		//}

		Player p = e.getPlayer();
		//Location ploc = e.getPlayer().getLocation();
		if (e.getAction() == Action.PHYSICAL) {


			//if (ploc.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {

			//Location loc = new Location(p.getWorld(), ploc.getX(), ploc.getY(), ploc.getZ());

			Location loc = e.getClickedBlock().getLocation();

			//JsonObject gate = new JsonHandler(plugin).JsonRead(null, loc).getAsJsonObject();
			List<String> xIndexList = util.IndexJson("x", String.valueOf(loc.getBlockX()), p);
			//List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
			//List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
			//List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
			String index = "0";
			for (String xindex : xIndexList) {
				//boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
				boolean y = util.getJson(xindex, "y", p).equalsIgnoreCase(String.valueOf(loc.getBlockY()));
				boolean z = util.getJson(xindex, "z", p).equalsIgnoreCase(String.valueOf(loc.getBlockZ()));
				boolean w = util.getJson(xindex, "world", p).equalsIgnoreCase(String.valueOf(loc.getWorld()));
				if (y && z && w) index = xindex;
			}

			if (!util.gateExists(index, null, p)) return;

			if (!(p.hasPermission("plategate.use"))) {
				p.sendMessage("§a[PlateGate] §c権限が不足しています。");
				return;
			}

			if (util.getJson(index, "to", p).equalsIgnoreCase("")) {
				e.setCancelled(false);
				e.getPlayer().sendMessage("§a[PlateGate] §bこのゲート "
						+ util.getJson(index, "name", p) + " はリンクされていません。");

				//spp.setPowered(false);

				return;
			}

			//JsonObject gateto = new JsonHandler(plugin).JsonRead(gate.get("to").getAsString(), null);
			String gateto = util.firstIndexJson("to", util.getJson(index, "name", p), p);

			float Yaw = 0;
			String rotate = util.getJson(index, "rotate", p);

			if (rotate.equalsIgnoreCase("north")) {
				Yaw = 180;
			} else if (rotate.equalsIgnoreCase("east")) {
				Yaw = 270;
			} else if (rotate.equalsIgnoreCase("south")) {
				Yaw = 0;
			} else if (rotate.equalsIgnoreCase("west")) {
				Yaw = 90;
			}

			Location toloc = new Location(Bukkit.getServer().getWorld(util.getJson(gateto, "world", p)),
					Integer.parseInt(util.getJson(gateto, "x", p)) + 0.5, Integer.parseInt(util.getJson(index, "y", p)),
					Integer.parseInt(util.getJson(gateto, "z", p)) + 0.5, Yaw, 0);


			if (rotate.equalsIgnoreCase("north")) {
				toloc.setZ(toloc.getZ() - 1);
			} else if (rotate.equalsIgnoreCase("east")) {
				toloc.setX(toloc.getX() + 1);
			} else if (rotate.equalsIgnoreCase("south")) {
				toloc.setZ(toloc.getZ() + 1);
			} else if (rotate.equalsIgnoreCase("west")) {
				toloc.setX(toloc.getX() - 1);
			}

			Location touploc = toloc.clone();
			touploc.setY(toloc.getY() + 1);
			touploc.getBlock().setType(Material.AIR);
			toloc.getBlock().setType(Material.AIR);

			p.teleport(toloc);




		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (e.getHand() == EquipmentSlot.OFF_HAND) return;
			
			if (e.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {
				if (!(p.hasPermission("plategate.gateinfo"))) {
					p.sendMessage("§a[PlateGate] §c権限が不足しています。");
					return;
				}

				Block b = e.getClickedBlock();
				Location loc = new Location(p.getWorld(), b.getX(), b.getY(), b.getZ());
				
				//JsonObject gate = new JsonHandler(plugin).JsonRead(null, loc);
				List<String> xIndexList = util.IndexJson("x", String.valueOf(loc.getBlockX()), p);
				//List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
				//List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
				//List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
				String gate = "0";
				for (String xindex : xIndexList) {
					//boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
					boolean y = util.getJson(xindex, "y", p).equalsIgnoreCase(String.valueOf(loc.getBlockY()));
					boolean z = util.getJson(xindex, "z", p).equalsIgnoreCase(String.valueOf(loc.getBlockZ()));
					boolean w = util.getJson(xindex, "world", p).equalsIgnoreCase(String.valueOf(loc.getWorld()));
					if (y && z && w) gate = xindex;
				}
				
				if (util.getJson(gate, "name", p).equalsIgnoreCase("null")) {
					
					//p.sendMessage("");
					
					return;
				}
				
				String facing = util.getJson(gate, "rotate", p);
				String yaw = "0";
				//yaw = gate.get("rotate").getAsString();

				if (facing.equalsIgnoreCase("south")){
					yaw = "0";
				} else if (facing.equalsIgnoreCase("west")){
					yaw = "90";
				} else if (facing.equalsIgnoreCase("north")){
					yaw = "180";
				} else if (facing.equalsIgnoreCase("east")){
					yaw = "-90";
				}
				
				String to;
				if (util.getJson(gate, "to", p).equalsIgnoreCase("")) {
					to = "§6None";
				} else {
					to = util.getJson(gate, "to", p);
				}
				
				p.sendMessage("§a[PlateGate]§b Name: §a" + util.getJson(gate, "name", p) + " §b Owner: §a"
							+ plugin.getServer().getPlayer(UUID.fromString(util.getJson(gate, "owner", p))).getName()
							+ " §b GoTo: §a" + to + " §b Rotate: §a" + facing + "§b (§a" + yaw + "§b)");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlateGateBlockBreak(BlockBreakEvent e) {
		
		if (e.getBlock().getType() == Material.IRON_BLOCK) {
			
			Player p = e.getPlayer();
			Block b = e.getBlock();
			Location loc = new Location(b.getWorld(), b.getX(), b.getY() + 1, b.getZ());
			
			//JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
			if (util.gateExists(util.IndexJson(loc, p), null, p)) {
				e.setCancelled(true);
				p.sendMessage("§a[PlateGate]§c PlateGateを壊すことはできません！");
			} else {
				e.setCancelled(false);
			}
			return;

		}
		if (e.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
			
			Player p = e.getPlayer();
			Block b = e.getBlock();
			Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
			
			//JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
			if (util.gateExists(util.IndexJson(loc, p), null, p)) {
				e.setCancelled(true);
				p.sendMessage("§a[PlateGate]§c PlateGateを壊すことはできません！");
			} else {
				e.setCancelled(false);
			}
			
		}
	}

	/*@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent e) {
		
		//System.out.println(0);
		List<Block> bl = e.getBlocks();
		for (Block b : bl) {
			//System.out.println(1);
			if (b.getType() == Material.IRON_BLOCK) {
				//System.out.println(2);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY() + 1, b.getZ());
				
				//JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				//e.setCancelled(!(glj.get("name").getAsString().equalsIgnoreCase("null")));

				List<String> xIndexList = util.IndexJson("x", String.valueOf(loc.getBlockX()), p);
				//List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
				//List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
				//List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
				String index = "0";
				for (String xindex : xIndexList) {
					//boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
					boolean y = util.getJson(xindex, "y", p).equalsIgnoreCase(String.valueOf(loc.getBlockY()));
					boolean z = util.getJson(xindex, "z", p).equalsIgnoreCase(String.valueOf(loc.getBlockZ()));
					boolean w = util.getJson(xindex, "world", p).equalsIgnoreCase(String.valueOf(loc.getWorld()));
					if (y && z && w) index = xindex;
				}
				e.setCancelled(util.gateExists(index, null, p));
				return;
				
			}
			if (b.getType() == Material.STONE_PRESSURE_PLATE) {
				//System.out.println(3);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
				
				JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				e.setCancelled(!(glj.get("name").getAsString().equalsIgnoreCase("null")));
				return;

			}
		}
	}
	
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent e) {
		
		//System.out.println(0);
		List<Block> bl = e.getBlocks();
		for (Block b : bl) {
			//System.out.println(1);
			if (b.getType() == Material.IRON_BLOCK) {
				//System.out.println(2);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY() + 1, b.getZ());
				
				JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				e.setCancelled(!(glj.get("name").getAsString().equalsIgnoreCase("null")));
				return;

			}
			if (b.getType() == Material.STONE_PRESSURE_PLATE) {
				//System.out.println(3);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
				
				JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				e.setCancelled(!(glj.get("name").getAsString().equalsIgnoreCase("null")));
				return;

			}
		}
	}*/
}

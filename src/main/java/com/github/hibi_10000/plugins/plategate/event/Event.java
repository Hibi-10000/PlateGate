package com.github.hibi_10000.plugins.plategate.event;

import java.util.List;
import java.util.UUID;

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

import com.google.gson.JsonObject;

import com.github.hibi_10000.plugins.plategate.JsonHandler;
import com.github.hibi_10000.plugins.plategate.PlateGate;

public class Event implements Listener {
	
	PlateGate plugin;
	public Event(PlateGate plategate) {
		plugin = plategate;
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

			if (!(p.hasPermission("plategate.use"))) {
				p.sendMessage("§a[PlateGate] §c権限が不足しています。");
				return;
			}
			
			//if (ploc.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
				
				//Location loc = new Location(p.getWorld(), ploc.getX(), ploc.getY(), ploc.getZ());
				
				Location loc = e.getClickedBlock().getLocation();
				
				JsonObject gate = new JsonHandler(plugin).JsonRead(null, loc).getAsJsonObject();
				
				if (gate.get("name").getAsString().equalsIgnoreCase("null")) {
					
					//p.sendMessage("");
					
					return;
				}
				
				//e.setCancelled(true);
				
				if (gate.get("to").getAsString().equalsIgnoreCase("")) {
					e.setCancelled(false);
					e.getPlayer().sendMessage("§a[PlateGate] §bこのゲート " 
							+ gate.get("name").getAsString() + " はリンクされていません。");
					
					//spp.setPowered(false);
					
					return;
				}
				
				JsonObject gateto = new JsonHandler(plugin).JsonRead(gate.get("to").getAsString(), null);
				
				int Yaw = 0;
				String rotate = gateto.get("rotate").getAsString();
				
				if (rotate.equalsIgnoreCase("north")) {
					Yaw = 180;
				} else if (rotate.equalsIgnoreCase("east")) {
					Yaw = 270;
				} else if (rotate.equalsIgnoreCase("south")) {
					Yaw = 0;
				} else if (rotate.equalsIgnoreCase("west")) {
					Yaw = 90;
				}
				
				Location toloc = new Location(Bukkit.getServer().getWorld(gateto.get("world").getAsString()),
						Integer.parseInt(gateto.get("x").getAsString()) + 0.5, Integer.parseInt(gateto.get("y").getAsString()),
						Integer.parseInt(gateto.get("z").getAsString()) + 0.5, Yaw, 0);
				
				
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
				return;
				
			//}
				
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (e.getHand() == EquipmentSlot.OFF_HAND) return;

			if (!(p.hasPermission("plategate.gateinfo"))) {
				p.sendMessage("§a[PlateGate] §c権限が不足しています。");
				return;
			}
			
			if (e.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {

				Block b = e.getClickedBlock();
				Location loc = new Location(p.getWorld(), b.getX(), b.getY(), b.getZ());
				
				JsonObject gate = new JsonHandler(plugin).JsonRead(null, loc);
				
				if (gate.get("name").getAsString().equalsIgnoreCase("null")) {
					
					//p.sendMessage("");
					
					return;
				}
				
				String yaw = gate.get("rotate").getAsString();
				String mcyaw = gate.get("rotate").getAsString();
				
				if (yaw.equalsIgnoreCase("south")){
					mcyaw = "0";
				} else if (yaw.equalsIgnoreCase("west")){
					mcyaw = "90";
				} else if (yaw.equalsIgnoreCase("north")){
					mcyaw = "180";
				} else if (yaw.equalsIgnoreCase("east")){
					mcyaw = "-90";
				} else {
					mcyaw= "0";
				}
				
				String to;
				if (gate.get("to").getAsString().equalsIgnoreCase("")) {
					to = "§6None";
				} else {
					to = gate.get("to").getAsString();
				}
				
				p.sendMessage("§a[PlateGate]§b Name: §a" + gate.get("name").getAsString() + " §b Owner: §a" 
							+ plugin.getServer().getPlayer(UUID.fromString(gate.get("owner").getAsString())).getName()
							+ " §b GoTo: §a" + to + " §b Rotate: §a" + yaw + "§b (§a" + mcyaw + "§b)");
				e.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlateGateBlockBreak(BlockBreakEvent e) {
		
		if (e.getBlock().getType() == Material.IRON_BLOCK) {
			
			Player p = e.getPlayer();
			Block b = e.getBlock();
			Location loc = new Location(b.getWorld(), b.getX(), b.getY() + 1, b.getZ());
			
			JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
			if (!(glj.get("name").getAsString().equalsIgnoreCase("null"))) {
				e.setCancelled(true);
				p.sendMessage("§a[PlateGate]§c これはPlateGateゲートです。 壊すことはできません。");
				return;
			} else {
				e.setCancelled(false);
				return;
			}
			
		}
		if (e.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {
			
			Player p = e.getPlayer();
			Block b = e.getBlock();
			Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
			
			JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
			if (!(glj.get("name").getAsString().equalsIgnoreCase("null"))) {
				e.setCancelled(true);
				p.sendMessage("§a[PlateGate]§c これはPlateGateです。 壊すことはできません。");
				return;
			} else {
				e.setCancelled(false);
				return;
			}
			
		}
	}
	
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent e) {
		
		//System.out.println(0);
		List<Block> bl = e.getBlocks();
		for (Block b : bl) {
			//System.out.println(1);
			if (b.getType() == Material.IRON_BLOCK) {
				//System.out.println(2);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY() + 1, b.getZ());
				
				JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				if (!(glj.get("name").getAsString().equalsIgnoreCase("null"))) {
					e.setCancelled(true);
					return;
					
				} else {
					e.setCancelled(false);
					return;
				}
				
			}
			if (b.getType() == Material.STONE_PRESSURE_PLATE) {
				//System.out.println(3);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
				
				JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				if (!(glj.get("name").getAsString().equalsIgnoreCase("null"))) {
					e.setCancelled(true);
					return;
				} else {
					e.setCancelled(false);
					return;
				}
				
			}
		}
		return;
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
				if (!(glj.get("name").getAsString().equalsIgnoreCase("null"))) {
					e.setCancelled(true);
					return;
					
				} else {
					e.setCancelled(false);
					return;
				}
				
			}
			if (b.getType() == Material.STONE_PRESSURE_PLATE) {
				//System.out.println(3);
				
				Location loc = new Location(b.getWorld(), b.getX(), b.getY(), b.getZ());
				
				JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
				if (!(glj.get("name").getAsString().equalsIgnoreCase("null"))) {
					e.setCancelled(true);
					return;
				} else {
					e.setCancelled(false);
					return;
				}
				
			}
		}
	}
}

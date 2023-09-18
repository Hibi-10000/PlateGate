/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.event

import com.github.hibi_10000.plugins.plategate.command.checkPermission
import com.github.hibi_10000.plugins.plategate.instance
import com.github.hibi_10000.plugins.plategate.dbUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*

class Event : Listener {
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        //if (!plugin.isEnabled()) return;
        //if (e.useInteractedBlock() == e.useInteractedBlock().DENY
        //		|| e.useItemInHand() == e.useItemInHand().DENY) return;
        //Powerable spp = (Powerable) e.getClickedBlock().getBlockData();
        //if ((spp.isPowered())) {
        //e.getPlayer().sendMessage("");
        //	return;
        //}
        val p = e.player
        //Location ploc = e.getPlayer().getLocation();
        if (e.action == Action.PHYSICAL) {


            //if (ploc.getBlock().getType() == Material.STONE_PRESSURE_PLATE) {

            //Location loc = new Location(p.getWorld(), ploc.getX(), ploc.getY(), ploc.getZ());
            val loc = e.clickedBlock!!.location

            //JsonObject gate = new JsonHandler(plugin).JsonRead(null, loc).getAsJsonObject();
            val xIndexList = dbUtil.allIndexJson("x", loc.blockX.toString(), p)
            //List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
            //List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
            //List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
            var index: String? = "0"
            for (xindex in xIndexList) {
                //boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
                val y = dbUtil.getJson(xindex, "y", p).equals(loc.blockY.toString(), ignoreCase = true)
                val z = dbUtil.getJson(xindex, "z", p).equals(loc.blockZ.toString(), ignoreCase = true)
                val w = dbUtil.getJson(xindex, "world", p).equals(loc.getWorld().toString(), ignoreCase = true)
                if (y && z && w) index = xindex
            }
            if (!dbUtil.gateExists(index, null, p)) return
            if (!checkPermission(p, "plategate.use")) return
            if (dbUtil.getJson(index!!, "to", p).equals("", ignoreCase = true)) {
                e.setCancelled(false)
                e.player.sendMessage("§a[PlateGate] §bこのゲート " + dbUtil.getJson(index, "name", p) + " はリンクされていません。")
                //spp.setPowered(false);
                return
            }

            //JsonObject gateto = new JsonHandler(plugin).JsonRead(gate.get("to").getAsString(), null);
            val gateto = dbUtil.firstIndexJson("to", dbUtil.getJson(index, "name", p), p)
            val rotate = dbUtil.getJson(index, "rotate", p)
            val yaw = when (rotate.lowercase()) {
                "north" -> 180f
                "east"  -> 270f
                "south" ->   0f
                "west"  ->  90f
                else    ->   0f
            }
            val toloc = Location(
                Bukkit.getServer().getWorld(dbUtil.getJson(gateto, "world", p)),
                dbUtil.getJson(gateto, "x", p).toInt() + 0.5, dbUtil.getJson(index, "y", p).toInt().toDouble(),
                dbUtil.getJson(gateto, "z", p).toInt() + 0.5, yaw, 0f
            )
            when (rotate.lowercase()) {
                "north" -> toloc.z -= 1
                "east"  -> toloc.x += 1
                "south" -> toloc.z += 1
                "west"  -> toloc.x -= 1
            }
            val touploc = toloc.clone()
            touploc.y = toloc.y + 1
            touploc.block.type = Material.AIR
            toloc.block.type = Material.AIR
            p.teleport(toloc)
        } else if (e.action == Action.RIGHT_CLICK_BLOCK) {
            if (e.hand == EquipmentSlot.OFF_HAND) return
            if (e.clickedBlock!!.type == Material.STONE_PRESSURE_PLATE) {
                if (!p.hasPermission("plategate.info")) {
                    p.sendMessage("§a[PlateGate] §c権限が不足しています。")
                    return
                }
                val b = e.clickedBlock
                val loc = Location(p.world, b!!.x.toDouble(), b.y.toDouble(), b.z.toDouble())

                //JsonObject gate = new JsonHandler(plugin).JsonRead(null, loc);
                val xIndexList = dbUtil.allIndexJson("x", loc.blockX.toString(), p)
                //List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
                //List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
                //List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
                var gate: String? = "0"
                for (xindex in xIndexList) {
                    //boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
                    val y = dbUtil.getJson(xindex, "y", p).equals(loc.blockY.toString(), ignoreCase = true)
                    val z = dbUtil.getJson(xindex, "z", p).equals(loc.blockZ.toString(), ignoreCase = true)
                    val w = dbUtil.getJson(xindex, "world", p).equals(loc.getWorld().toString(), ignoreCase = true)
                    if (y && z && w) gate = xindex
                }
                if (dbUtil.getJson(gate!!, "name", p).equals("null", ignoreCase = true)) {

                    //p.sendMessage("");
                    return
                }
                val facing = dbUtil.getJson(gate, "rotate", p)
                val yaw = when (facing.lowercase()) {
                    "south" ->   "0"
                    "west"  ->  "90"
                    "north" -> "180"
                    "east"  -> "-90"
                    else    ->   "0"
                }
                val to: String = if (dbUtil.getJson(gate, "to", p).equals("", ignoreCase = true)) {
                    "§6None"
                } else {
                    dbUtil.getJson(gate, "to", p)
                }
                p.sendMessage(
                    "§a[PlateGate]§b Name: §a" + dbUtil.getJson(gate, "name", p) + " §b Owner: §a"
                            + instance!!.server.getPlayer(UUID.fromString(dbUtil.getJson(gate, "owner", p)))!!.name
                            + " §b GoTo: §a" + to + " §b Rotate: §a" + facing + "§b (§a" + yaw + "§b)"
                )
                e.setCancelled(true)
            }
        }
    }

    @EventHandler
    fun onPlateGateBlockBreak(e: BlockBreakEvent) {
        if (e.block.type == Material.IRON_BLOCK) {
            val p = e.player
            val b = e.block
            val loc = Location(b.world, b.x.toDouble(), (b.y + 1).toDouble(), b.z.toDouble())

            //JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
            if (dbUtil.gateExists(dbUtil.allIndexJson(loc, p), null, p)) {
                e.isCancelled = true
                p.sendMessage("§a[PlateGate]§c PlateGateを壊すことはできません！")
            } else {
                e.isCancelled = false
            }
            return
        }
        if (e.block.type == Material.STONE_PRESSURE_PLATE) {
            val p = e.player
            val b = e.block
            val loc = Location(b.world, b.x.toDouble(), b.y.toDouble(), b.z.toDouble())

            //JsonObject glj = new JsonHandler(plugin).JsonRead(null, loc);
            if (dbUtil.gateExists(dbUtil.allIndexJson(loc, p), null, p)) {
                e.isCancelled = true
                p.sendMessage("§a[PlateGate]§c PlateGateを壊すことはできません！")
            } else {
                e.isCancelled = false
            }
        }
    } /*@EventHandler
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

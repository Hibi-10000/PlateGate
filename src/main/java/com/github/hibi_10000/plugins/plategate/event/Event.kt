package com.github.hibi_10000.plugins.plategate.event

import com.github.hibi_10000.plugins.plategate.instance
import com.github.hibi_10000.plugins.plategate.util.Util
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
    private val util: Util = Util()

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
            val xIndexList = util.IndexJson("x", loc.blockX.toString(), p)
            //List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
            //List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
            //List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
            var index: String? = "0"
            for (xindex in xIndexList) {
                //boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
                val y = util.getJson(xindex, "y", p).equals(loc.blockY.toString(), ignoreCase = true)
                val z = util.getJson(xindex, "z", p).equals(loc.blockZ.toString(), ignoreCase = true)
                val w = util.getJson(xindex, "world", p).equals(loc.getWorld().toString(), ignoreCase = true)
                if (y && z && w) index = xindex
            }
            if (!util.gateExists(index, null, p)) return
            if (!p.hasPermission("plategate.use")) {
                p.sendMessage("§a[PlateGate] §c権限が不足しています。")
                return
            }
            if (util.getJson(index!!, "to", p).equals("", ignoreCase = true)) {
                e.setCancelled(false)
                e.player.sendMessage(
                    "§a[PlateGate] §bこのゲート "
                            + util.getJson(index, "name", p) + " はリンクされていません。"
                )

                //spp.setPowered(false);
                return
            }

            //JsonObject gateto = new JsonHandler(plugin).JsonRead(gate.get("to").getAsString(), null);
            val gateto = util.firstIndexJson("to", util.getJson(index, "name", p), p)
            var Yaw = 0f
            val rotate = util.getJson(index, "rotate", p)
            if (rotate.equals("north", ignoreCase = true)) {
                Yaw = 180f
            } else if (rotate.equals("east", ignoreCase = true)) {
                Yaw = 270f
            } else if (rotate.equals("south", ignoreCase = true)) {
                Yaw = 0f
            } else if (rotate.equals("west", ignoreCase = true)) {
                Yaw = 90f
            }
            val toloc = Location(
                Bukkit.getServer().getWorld(util.getJson(gateto, "world", p)),
                util.getJson(gateto, "x", p).toInt() + 0.5, util.getJson(index, "y", p).toInt().toDouble(),
                util.getJson(gateto, "z", p).toInt() + 0.5, Yaw, 0f
            )
            if (rotate.equals("north", ignoreCase = true)) {
                toloc.z = toloc.z - 1
            } else if (rotate.equals("east", ignoreCase = true)) {
                toloc.x = toloc.x + 1
            } else if (rotate.equals("south", ignoreCase = true)) {
                toloc.z = toloc.z + 1
            } else if (rotate.equals("west", ignoreCase = true)) {
                toloc.x = toloc.x - 1
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
                val xIndexList = util.IndexJson("x", loc.blockX.toString(), p)
                //List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
                //List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
                //List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
                var gate: String? = "0"
                for (xindex in xIndexList) {
                    //boolean x = util.getJson(xindex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
                    val y = util.getJson(xindex, "y", p).equals(loc.blockY.toString(), ignoreCase = true)
                    val z = util.getJson(xindex, "z", p).equals(loc.blockZ.toString(), ignoreCase = true)
                    val w = util.getJson(xindex, "world", p).equals(loc.getWorld().toString(), ignoreCase = true)
                    if (y && z && w) gate = xindex
                }
                if (util.getJson(gate!!, "name", p).equals("null", ignoreCase = true)) {

                    //p.sendMessage("");
                    return
                }
                val facing = util.getJson(gate, "rotate", p)
                var yaw = "0"
                //yaw = gate.get("rotate").getAsString();
                if (facing.equals("south", ignoreCase = true)) {
                    yaw = "0"
                } else if (facing.equals("west", ignoreCase = true)) {
                    yaw = "90"
                } else if (facing.equals("north", ignoreCase = true)) {
                    yaw = "180"
                } else if (facing.equals("east", ignoreCase = true)) {
                    yaw = "-90"
                }
                val to: String = if (util.getJson(gate, "to", p).equals("", ignoreCase = true)) {
                    "§6None"
                } else {
                    util.getJson(gate, "to", p)
                }
                p.sendMessage(
                    "§a[PlateGate]§b Name: §a" + util.getJson(gate, "name", p) + " §b Owner: §a"
                            + instance!!.server.getPlayer(UUID.fromString(util.getJson(gate, "owner", p)))!!.name
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
            if (util.gateExists(util.IndexJson(loc, p), null, p)) {
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
            if (util.gateExists(util.IndexJson(loc, p), null, p)) {
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
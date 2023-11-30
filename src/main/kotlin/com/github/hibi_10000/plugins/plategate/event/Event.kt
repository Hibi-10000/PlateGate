/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.event

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*

class Event : Listener {
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        /*
        if (e.useInteractedBlock() == e.useInteractedBlock().DENY || e.useItemInHand() == e.useItemInHand().DENY) return
        val spp = e.clickedBlock?.blockData as Powerable
        if (spp.isPowered()) {
            e.player.sendMessage("")
            return
        }
        */
        val p = e.player
        if (e.action == Action.PHYSICAL) {
            if (!util.checkPermission(p, "plategate.use")) return

            if (e.clickedBlock?.type != Material.STONE_PRESSURE_PLATE) return
            val index = dbUtil.allIndexJson(e.clickedBlock!!, null)
            if (!dbUtil.gateExists(index, null, p)) return

            if (dbUtil.getJson(index!!, "to", p).equals("")) {
                e.setCancelled(false)
                e.player.sendMessage("§a[PlateGate] §bこのゲート ${dbUtil.getJson(index, "name", p)} はリンクされていません。")
                //spp.setPowered(false);
                return
            }

            val gateTo = dbUtil.firstIndexJson("name", dbUtil.getJson(index, "to", p)!!, p) ?: return
            val rotate = dbUtil.getJson(gateTo, "rotate", p)!!
            val toLoc = dbUtil.gateLocation(gateTo, p)
            toLoc.pitch = p.location.pitch
            toLoc.x += 0.5
            toLoc.z += 0.5
            when (rotate.lowercase()) {
                "north" -> toLoc.z -= 1
                "east"  -> toLoc.x += 1
                "south" -> toLoc.z += 1
                "west"  -> toLoc.x -= 1
            }
            util.upperBlock(toLoc.block).type = Material.AIR
            toLoc.block.type = Material.AIR
            p.teleport(toLoc)
        } else if (e.action == Action.RIGHT_CLICK_BLOCK) {
            if (e.hand == EquipmentSlot.OFF_HAND) return
            if (e.clickedBlock?.type == Material.STONE_PRESSURE_PLATE) {
                if (!util.checkPermission(p, "plategate.info")) return

                val gate: String? = dbUtil.allIndexJson(e.clickedBlock!!, null)
                if (!dbUtil.gateExists(gate, null, p)) return
                val owner = util.getOfflinePlayer(UUID.fromString(dbUtil.getJson(gate!!, "owner", p)), p)

                val facing = dbUtil.getJson(gate, "rotate", p)!!
                val yaw = when (facing.lowercase()) {
                    "south" ->   "0"
                    "west"  ->  "90"
                    "north" -> "180"
                    "east"  -> "-90"
                    else    ->   "0"
                }
                val to = if (dbUtil.getJson(gate, "to", p).equals("")) "§6None" else dbUtil.getJson(gate, "to", p)!!
                p.sendMessage(
                    "§a[PlateGate]§b Name: §a${dbUtil.getJson(gate, "name", p)} §b Owner: §a${owner.name
                    } §b GoTo: §a$to §b Rotate: §a$facing§b (§a$yaw§b)"
                )
                e.setCancelled(true)
            }
        }
    }

    @EventHandler
    fun onPlateGateBlockBreak(e: BlockBreakEvent) {
        e.isCancelled = isPlateGateBlock(listOf(e.block), e.player)
        if (e.isCancelled) e.player.sendMessage("§a[PlateGate]§c PlateGateを壊すことはできません！")
    }

    @EventHandler
	fun onPistonExtend(e: BlockPistonExtendEvent) {
        e.isCancelled = isPlateGateBlock(e.blocks, null)
	}

	@EventHandler
	fun onPistonRetract(e: BlockPistonRetractEvent) {
        e.isCancelled = isPlateGateBlock(e.blocks, null)
	}

    private fun isPlateGateBlock(blocks: List<Block>, player: Player?): Boolean {
        var isPlateGateBlock = false
        for (b in blocks) {
            if (b.type == Material.IRON_BLOCK) {
                isPlateGateBlock = dbUtil.gateExists(dbUtil.allIndexJson(util.upperBlock(b), null), null, player)
            }
            else if (b.type == Material.STONE_PRESSURE_PLATE) {
                isPlateGateBlock = dbUtil.gateExists(dbUtil.allIndexJson(b, null), null, player)
            }
            if (isPlateGateBlock) break
        }
        return isPlateGateBlock
    }
}

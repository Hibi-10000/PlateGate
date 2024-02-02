/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.event

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class Event : Listener {
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val p = e.player
        if (e.hand == EquipmentSlot.OFF_HAND) return
        if (e.action != Action.PHYSICAL && e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.clickedBlock?.type != Material.STONE_PRESSURE_PLATE) return
        val gate: CraftPlateGate
        try {
            val block = e.clickedBlock ?: throw NullPointerException()
            gate = dbUtil.get(block.world.uid, block.x, block.y, block.z)
        } catch (e: Exception) {
            if (e.message != "gateNotFound") p.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return
        }
        //TODO: When文に変更?
        if (e.action == Action.PHYSICAL) {
            if (!util.checkPermission(p, "plategate.use")) return

            if (gate.to == null) {
                e.player.sendMessage("§a[PlateGate] §bこのゲート ${gate.name} はリンクされていません。")
                return
            }
            val gateTo: CraftPlateGate
            try {
                gateTo = dbUtil.get(gate.owner, gate.to ?: throw NullPointerException())
            } catch (e: Exception) {
                if (e.message == "gateNotFound") p.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
                else p.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
                return
            }

            val toBlock = gateTo.getTPLocationBlock()
            if (toBlock == null) {
                p.sendMessage("§a[PlateGate] §cワールドが見つかりませんでした")
                return
            }
            util.upperBlock(toBlock).type = Material.AIR
            toBlock.type = Material.AIR
            val toLoc = toBlock.location
            toLoc.pitch = p.location.pitch
            //toLoc.x += 0.5
            //toLoc.z += 0.5
            p.teleport(toLoc)
        } else if (e.action == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true)
            if (!util.checkPermission(p, "plategate.info")) return

            val owner = util.getOfflinePlayer(gate.owner, p) ?: return
            val facing = gate.rotate
            val yaw = when (facing) {
                BlockFace.SOUTH ->   "0"
                BlockFace.WEST  ->  "90"
                BlockFace.NORTH -> "180"
                BlockFace.EAST  -> "-90"
                else            ->   "0"
            }
            p.sendMessage(
                "§a[PlateGate]§b Name: §a${gate.name}§b Owner: §a${owner.name
                }§b To: §a${gate.to ?: "null"}§b Rotate: §a${facing}§b (§a${yaw}§b)"
            )
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
        for (b in blocks) {
            try {
                when (b.type) {
                    Material.IRON_BLOCK -> {
                        val ub = util.upperBlock(b)
                        dbUtil.get(ub.world.uid, ub.x, ub.y, ub.z)
                        return true
                    }
                    Material.STONE_PRESSURE_PLATE -> {
                        dbUtil.get(b.world.uid, b.x, b.y, b.z)
                        return true
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                if (e.message != "gateNotFound") player?.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            }
        }
        return false
    }
}

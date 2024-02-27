/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.event

import com.github.hibi_10000.plugins.plategate.*
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.localization.Message
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot

object Event: Listener {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.hand == EquipmentSlot.OFF_HAND) return
        val p = e.player
        if (e.action != Action.PHYSICAL && ((e.action != Action.RIGHT_CLICK_BLOCK) || p.isSneaking)) return
        if (e.clickedBlock?.type != Material.STONE_PRESSURE_PLATE) return
        val gate: CraftPlateGate
        try {
            val block = e.clickedBlock!!
            gate = dbUtil.get(block.world.uid, block.x, block.y, block.z)
        } catch (e: Exception) {
            if (e !is DBUtil.GateNotFoundException) MessageUtil.catchUnexpectedError(p, e)
            return
        }
        //TODO: When文に変更?
        if (e.action == Action.PHYSICAL) {
            usePlateGate(p, gate)
        } else if (e.action == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true)
            clickPlateGate(p, gate)
        }
    }

    private fun usePlateGate(p: Player, gate: CraftPlateGate) {
        if (!Util.checkPermission(p, "plategate.use")) return
        if (noInteract.contains(p.uniqueId)) return

        if (gate.toOwner == null || gate.toName == null) {
            p.sendMessage("§a[PlateGate] §bこのゲート ${gate.name} はリンクされていません。")
            return
        }
        val gateTo: CraftPlateGate
        try {
            gateTo = dbUtil.get(gate.toOwner!!, gate.toName!!)
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) MessageUtil.sendError(p, Message.ERROR_GATE_NOT_FOUND)
            else MessageUtil.catchUnexpectedError(p, e)
            return
        }

        val toBlock = gateTo.getTPLocationBlock()
        if (toBlock == null) {
            MessageUtil.sendError(p, Message.ERROR_WORLD_NOT_FOUND)
            return
        }
        Util.upperBlock(toBlock).type = Material.AIR
        toBlock.type = Material.AIR
        val toLoc = Util.getBlockCenter(toBlock)
        toLoc.yaw = Util.convBlockFace2Yaw(gateTo.rotate)
        toLoc.pitch = p.location.pitch
        p.teleport(toLoc)
    }

    private fun clickPlateGate(p: Player, gate: CraftPlateGate) {
        if (!Util.checkPermission(p, "plategate.info")) return

        val owner = Util.getOfflinePlayer(gate.owner, p) ?: return
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
            }§b To: §a${gate.toName ?: "null"} ${gate.toOwner?.let { Util.getOfflinePlayer(it, null) }?.name ?: "null"
            }§b Rotate: §a${Util.firstUpperCase(facing.name)}§b (§a${yaw}§b)"
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(e: BlockBreakEvent) {
        if (isPlateGateBlock(e.block, e.player)) {
            e.isCancelled = true
            MessageUtil.sendActionBarError(e.player, Message.ERROR_BREAK_GATE_BLOCK)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockDamage(e: BlockDamageEvent) {
        if (isPlateGateBlock(e.block, e.player)) {
            MessageUtil.sendActionBarError(e.player, Message.ERROR_BREAK_GATE_BLOCK)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockExplode(e: BlockExplodeEvent) {
        for (b in e.blockList().toMutableList()) {
            if (isPlateGateBlock(b, null)) {
                e.blockList().remove(b)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityExplode(e: EntityExplodeEvent) {
        for (b in e.blockList().toMutableList()) {
            if (isPlateGateBlock(b, null)) {
                e.blockList().remove(b)
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
	fun onPistonExtend(e: BlockPistonExtendEvent) {
        e.isCancelled = isPlateGateBlock(e.blocks)
	}

	@EventHandler(ignoreCancelled = true)
	fun onPistonRetract(e: BlockPistonRetractEvent) {
        e.isCancelled = isPlateGateBlock(e.blocks)
	}

    private fun isPlateGateBlock(blocks: List<Block>): Boolean {
        for (b in blocks) {
            if (isPlateGateBlock(b, null)) return true
        }
        return false
    }

    private fun isPlateGateBlock(block: Block, player: Player?): Boolean {
        if (block.type != Material.IRON_BLOCK && block.type != Material.STONE_PRESSURE_PLATE) return false
        return try {
            val b = if (block.type == Material.IRON_BLOCK) Util.upperBlock(block) else block
            dbUtil.get(b.world.uid, b.x, b.y, b.z)
            true
        } catch (e: Exception) {
            if (e !is DBUtil.GateNotFoundException) MessageUtil.catchUnexpectedError(player, e)
            false
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val p = e.player
        val gate = transfer[p.uniqueId] ?: return
        transfer.remove(p.uniqueId)
        //TODO: Asyncで動くのか確認する
        Bukkit.getScheduler().runTaskLaterAsynchronously(instance, Runnable {
            val op = Util.getPlayer(gate.owner, null) ?: return@Runnable
            MessageUtil.sendErrorMessage(op, "${p.name} が退出したため、ゲートの所有権の譲渡要求がキャンセルされました")
        }, 20L)
    }
}

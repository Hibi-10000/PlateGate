/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.instance
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGJump {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.jump")) return false
        if (args.size != 2) return util.commandInvalid(sender, label)

        val gate: CraftPlateGate
        try {
            gate = dbUtil.get(sender.uniqueId, args[1])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) sender.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
            else sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return false
        }
        val toBlock = gate.getTPLocationBlock()
        if (toBlock == null) {
            sender.sendMessage("§a[PlateGate] §cワールドが見つかりませんでした")
            return false
        }
        toBlock.type = Material.AIR
        util.upperBlock(toBlock).type = Material.AIR
        val toLoc = util.getBlockCenter(toBlock)
        toLoc.yaw = util.convBlockFace2Yaw(gate.rotate)
        toLoc.pitch = sender.location.pitch
        sender.teleport(toLoc)
        sender.sendMessage("§a[PlateGate] §bゲート ${args[1]} にジャンプしました。")
        instance.logger.info("§b${sender.name} がゲート ${args[1]} にジャンプしました。")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

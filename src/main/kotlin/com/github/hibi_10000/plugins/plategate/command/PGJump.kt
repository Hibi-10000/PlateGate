/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.MessageUtil
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGJump {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, cmd: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.jump")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        val gate: CraftPlateGate
        try {
            gate = dbUtil.get(sender.uniqueId, args[1])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) MessageUtil.sendErrorMessage(sender, "ゲートが見つかりませんでした")
            else MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        val toBlock = gate.getTPLocationBlock()
        if (toBlock == null) {
            MessageUtil.sendErrorMessage(sender, "ワールドが見つかりませんでした")
            return false
        }
        toBlock.type = Material.AIR
        Util.upperBlock(toBlock).type = Material.AIR
        val toLoc = Util.getBlockCenter(toBlock)
        toLoc.yaw = Util.convBlockFace2Yaw(gate.rotate)
        toLoc.pitch = sender.location.pitch
        sender.teleport(toLoc)
        MessageUtil.sendMessage(sender, "ゲート ${args[1]} にジャンプしました。")
        MessageUtil.logInfo("${sender.name} がゲート ${args[1]} にジャンプしました。")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

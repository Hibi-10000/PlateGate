/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.*
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGRemove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.remove")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        val gate: CraftPlateGate
        try {
            gate = dbUtil.get(sender.uniqueId, args[1])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) sender.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
            else sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return false
        }
        val toBlock = gate.getBlock()
        if (toBlock == null) {
            sender.sendMessage("§a[PlateGate] §cワールドが見つかりませんでした")
            return false
        }
        toBlock.type = Material.AIR
        val toUnderBlock = Util.underBlock(toBlock)
        toUnderBlock.type = gate.beforeBlock
        try {
            dbUtil.remove(sender.uniqueId, args[1])
        } catch (e: Exception) {
            sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return false
        }
        MessageUtil.sendMessage(sender, "Gate:${gate.name} を削除しました。")
        instance.logger.info("${sender.name} が Gate:${gate.name} を削除しました。")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

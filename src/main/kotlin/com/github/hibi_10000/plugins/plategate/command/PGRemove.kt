/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.jsonUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGRemove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.remove")) return false
        if (args.size != 2) return util.commandInvalid(sender, label)

        val gate: CraftPlateGate
        try {
            gate = jsonUtil.get(args[1], sender.uniqueId.toString())
        } catch (e: Exception) {
            if (e.message == "gateNotFound") sender.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
            else sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return false
        }
        val toBlock = gate.getBlock()
        if (toBlock == null) {
            sender.sendMessage("§a[PlateGate] §cワールドが見つかりませんでした")
            return false
        }
        toBlock.type = Material.AIR
        val toUnderBlock = util.underBlock(toBlock)
        toUnderBlock.type = gate.beforeBlock
        try {
            jsonUtil.remove(args[1], sender.uniqueId.toString())
        } catch (e: Exception) {
            sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
            return false
        }
        sender.sendMessage("§a[PlateGate] §bGate:${gate.name} を削除しました。")
        println("§a[PlateGate] §b${sender.name} が Gate:${gate.name} を削除しました。")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

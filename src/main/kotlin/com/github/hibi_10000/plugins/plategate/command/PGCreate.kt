/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGCreate {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.create")) return false
        if (args.size != 2) return util.commandInvalid(sender, label)

        val loc = sender.location.clone()
        loc.pitch = 0f
        if (loc.y != loc.blockY.toDouble()) {
            sender.sendMessage("§a[PlateGate]§c 下のブロックはフルブロックである必要があります。")
            return false
        }
        if (loc.block.type != Material.AIR) {
            sender.sendMessage("§a[PlateGate]§c その場所の非フルブロックを取り除いてください。")
            return false
        }
        try {
            dbUtil.add(
                CraftPlateGate(
                    sender.uniqueId,
                    args[1],
                    loc.block,
                    sender.facing,
                    null,
                    null
                )
            )
        } catch (e: Exception) {
            if (e is DBUtil.GateNameDuplicateException) sender.sendMessage("§a[PlateGate]§c \"${args[1]}\"は既に使用されています。")
            else sender.sendMessage("§a[PlateGate]§c 予期せぬエラーが発生しました。")
            return false
        }
        util.noInteract(sender.uniqueId)
        val underBlock = util.underBlock(loc.block)
        loc.block.type = Material.STONE_PRESSURE_PLATE
        underBlock.type = Material.IRON_BLOCK
        sender.sendMessage("§a[PlateGate] §bPlateGate ${args[1]} を $loc に作成しました")
        println("§a[PlateGate] §b${sender.name} がPlateGate ${args[1]} を $loc に作成しました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGCreate {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.create")) return false
        if (args.size != 2) return util.commandInvalid(sender, label)

        if (dbUtil.isDuplicateName(args[1], sender as Player)) return false
        val loc = sender.location.clone()
        if (loc.block.type != Material.AIR) {
            sender.sendMessage("§a[PlateGate]§c その場所の非フルブロックを取り除いてください。")
            return false
        }
        if (loc.y != loc.blockY.toDouble()) {
            sender.sendMessage("§a[PlateGate]§c 下のブロックはフルブロックである必要があります。")
            return false
        }
        val underLoc = util.underLocation(sender.location)
        val beforeUnderBlock = underLoc.block.type
        underLoc.block.type = Material.IRON_BLOCK
        loc.block.type = Material.STONE_PRESSURE_PLATE
        //val poweredBlockData = Material.STONE_PRESSURE_PLATE.createBlockData() as Powerable
        //poweredBlockData.isPowered = true
        //loc.block.blockData = poweredBlockData

        val d = util.convBlockFace2Facing(sender.facing)
        dbUtil.addJson(
            arrayOf(
                args[1],
                sender.name,
                "",
                loc.blockX.toString(),
                loc.blockY.toString(),
                loc.blockZ.toString(),
                d,
                loc.getWorld().toString(),
                beforeUnderBlock.name
            ), sender
        )
        sender.sendMessage("§a[PlateGate] §bPlateGate " + args[1] + " を " + loc + " に作成しました")
        println("§a[PlateGate] §b" + sender.name + " がPlateGate " + args[1] + " を " + loc + " に作成しました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        //List<String> list = new ArrayList<>();
        //list.removeAll(list);


        //list.removeAll(list);
        return null
    }
}

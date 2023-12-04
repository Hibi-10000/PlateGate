/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGMove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.move")) return false
        if (args.size != 2) return util.commandInvalid(sender, label)

        var index = dbUtil.firstIndexJson("name", args[1], sender as Player) ?: return false
        val owner = util.getOfflinePlayer(dbUtil.getJson(index, "owner", sender)!!, null)!!
        if (owner.uniqueId.toString() != sender.uniqueId.toString()) {
            sender.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。")
            return false
        }

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
        val underBlock = util.underBlock(loc.block)
        val beforeUnderBlock = underBlock.type
        loc.block.type = Material.STONE_PRESSURE_PLATE
        underBlock.type = Material.IRON_BLOCK
        /*
        Powerable blockData = (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData();
        blockData.setPowered(false);
        loc.getBlock().setBlockData(blockData);
        */

        val oldLoc = dbUtil.gateLocation(index, sender)
        val oldUnderLoc = Location(oldLoc.world, oldLoc.blockX.toDouble(), (oldLoc.blockY - 1).toDouble(), oldLoc.blockZ.toDouble())
        oldLoc.block.type = Material.AIR
        oldUnderLoc.block.type = dbUtil.underBlock(index, sender)

        val rotate = util.convBlockFace2Facing(sender.facing)
        index = dbUtil.firstIndexJson("name", args[1], sender) ?: return false
        dbUtil.setJson(index, "x"          , loc.blockX.toString(), sender)
        dbUtil.setJson(index, "y"          , loc.blockY.toString(), sender)
        dbUtil.setJson(index, "z"          , loc.blockZ.toString(), sender)
        dbUtil.setJson(index, "rotate"     , rotate               , sender)
        dbUtil.setJson(index, "world"      , sender.world.name    , sender)
        dbUtil.setJson(index, "beforeBlock", beforeUnderBlock.name, sender)
        sender.sendMessage("§a[PlateGate] §bゲート ${args[1]} を $loc に移動しました")
        println("§a[PlateGate] §b${sender.name} がゲート ${args[1]} を $loc に移動しました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

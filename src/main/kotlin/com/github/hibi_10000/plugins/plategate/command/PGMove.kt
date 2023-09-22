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

        if (!dbUtil.gateExists(null, args[1], (sender as Player))) return false
        val ploc = sender.location
        val loc = Location(ploc.getWorld(), ploc.x, ploc.y, ploc.z, ploc.yaw, 0f)
        val downloc = Location(sender.world, loc.x, loc.y - 1, loc.z, loc.yaw, 0f)
        if (loc.block.type != Material.AIR) {
            sender.sendMessage("§a[PlateGate]§c その場所の非フルブロックを取り除いてください。")
            return false
        }
        if (loc.y != loc.blockY.toDouble()) {
            sender.sendMessage("§a[PlateGate]§c 下のブロックはフルブロックである必要があります。")
            return false
        }
        val downblockbefore = downloc.block.type
        downloc.block.type = Material.IRON_BLOCK
        loc.block.type = Material.STONE_PRESSURE_PLATE
        //Powerable ppbd = (Powerable) Material.STONE_PRESSURE_PLATE.createBlockData();
        //ppbd.setPowered(false);
        //loc.getBlock().setBlockData(ppbd);


        //JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null);
        var index = dbUtil.firstIndexJson("name", args[1], sender)
        val oldloc = dbUtil.gateLocation(index, sender)
        val olddownloc =
            Location(sender.world, oldloc.blockX.toDouble(), (oldloc.blockY - 1).toDouble(), oldloc.blockZ.toDouble())
        oldloc.block.type = Material.AIR
        olddownloc.block.type = Material.getMaterial(dbUtil.getJson(index, "beforeblock", sender)!!)!!

        //new JsonHandler(plugin).JsonChange(args[1], null, null, null, loc, downblockbefore, p);
        //float yaw = loc.getYaw();
        val d = util.convBlockFace2Facing(sender.facing)
        index = dbUtil.firstIndexJson("name", args[1], sender)
        dbUtil.setJson(index, "x", loc.blockX.toString(), sender)
        dbUtil.setJson(index, "y", loc.blockY.toString(), sender)
        dbUtil.setJson(index, "z", loc.blockZ.toString(), sender)
        dbUtil.setJson(index, "rotate", d, sender)
        dbUtil.setJson(index, "world", sender.world.name, sender)
        dbUtil.setJson(index, "beforeblock", downblockbefore.toString(), sender)
        sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " を " + loc + " に移動しました")
        println("§a[PlateGate] §b" + sender.name + " がゲート " + args[1] + " を " + loc + " に移動しました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.MessageUtil
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.localization.Message
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGMove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.move")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        val loc = sender.location.clone()
        val underBlock = Util.underBlock(loc.block)
        if (!Util.checkCreateLocation(sender, loc, underBlock)) return false
        loc.pitch = 0f

        val oldGate = try {
            dbUtil.get(sender.uniqueId, args[1])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) MessageUtil.sendError(sender, Message.ERROR_GATE_NOT_FOUND)
            else MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        val oldBlock = oldGate.getBlock()
        if (oldBlock == null) {
            MessageUtil.sendError(sender, Message.ERROR_WORLD_NOT_FOUND)
            return false
        }
        try {
            dbUtil.move(
                CraftPlateGate(
                    sender.uniqueId,
                    args[1],
                    loc.block,
                    sender.facing,
                    oldGate.toOwner,
                    oldGate.toName,
                )
            )
        } catch (e: Exception) {
            if (e is DBUtil.GateLocationDuplicateException) MessageUtil.sendError(sender, Message.ERROR_GATE_LOCATION_INTERFERENCE)
            else MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        val oldUnderBlock = Util.underBlock(oldBlock)
        oldBlock.type = Material.AIR
        oldUnderBlock.type = oldGate.beforeBlock
        Util.noInteract(sender.uniqueId)
        loc.block.type = Material.STONE_PRESSURE_PLATE
        underBlock.type = Material.IRON_BLOCK
        MessageUtil.send(sender, Message.COMMAND_MOVE_SUCCESS, args[1], loc.toString())
        MessageUtil.logInfo(Message.COMMAND_MOVE_SUCCESS_LOG, sender.name, args[1], loc.toString())
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

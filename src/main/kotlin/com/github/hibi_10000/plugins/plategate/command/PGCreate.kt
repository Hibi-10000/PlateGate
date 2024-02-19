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

object PGCreate {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.create")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        val loc = sender.location.clone()
        val underBlock = Util.underBlock(loc.block)
        if (!Util.checkCreateLocation(sender, loc, underBlock)) return false
        loc.pitch = 0f
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
            when (e) {
                is DBUtil.GateNameDuplicateException -> MessageUtil.sendErrorMessage(sender, "\"${args[1]}\"は既に使用されています")
                is DBUtil.GateLocationDuplicateException -> MessageUtil.sendErrorMessage(sender, "その場所は他のゲートと干渉します")
                else -> MessageUtil.catchUnexpectedError(sender, e)
            }
            return false
        }
        Util.noInteract(sender.uniqueId)
        loc.block.type = Material.STONE_PRESSURE_PLATE
        underBlock.type = Material.IRON_BLOCK
        MessageUtil.Message.COMMANDS_CREATE_SUCCESS.send(sender, args[1], loc.toString())
        MessageUtil.Message.COMMANDS_CREATE_SUCCESS_LOG.logInfo(sender.name, args[1], loc.toString())
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

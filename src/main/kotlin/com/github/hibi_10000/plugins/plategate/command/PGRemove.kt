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

object PGRemove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.remove")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        val gate: CraftPlateGate
        try {
            gate = dbUtil.get(sender.uniqueId, args[1])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) MessageUtil.sendError(sender, Message.ERROR_GATE_NOT_FOUND)
            else MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        val toBlock = gate.getBlock()
        if (toBlock == null) {
            MessageUtil.sendErrorMessage(sender, "ワールドが見つかりませんでした")
            return false
        }
        toBlock.type = Material.AIR
        val toUnderBlock = Util.underBlock(toBlock)
        toUnderBlock.type = gate.beforeBlock
        try {
            dbUtil.remove(sender.uniqueId, args[1])
        } catch (e: Exception) {
            MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        MessageUtil.send(sender, Message.COMMAND_REMOVE_SUCCESS, gate.name)
        MessageUtil.logInfo(Message.COMMAND_REMOVE_SUCCESS_LOG, sender.name, gate.name)
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

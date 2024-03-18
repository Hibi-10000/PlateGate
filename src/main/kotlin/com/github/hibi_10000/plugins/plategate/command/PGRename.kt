/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.MessageUtil
import com.github.hibi_10000.plugins.plategate.Util
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.localization.Message
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGRename {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.rename")) return false
        if (args.size != 3) return Util.commandInvalid(sender, label)

        val gate = try {
            dbUtil.rename(sender.uniqueId, args[1], args[2])
        } catch (e: Exception) {
            when (e) {
                is DBUtil.GateNameDuplicateException -> MessageUtil.send(sender, Message.ERROR_GATE_NAME_ALREADY_USED, args[2])
                is DBUtil.GateNotFoundException -> MessageUtil.send(sender, Message.ERROR_GATE_NOT_FOUND)
                else -> MessageUtil.catchUnexpectedError(sender, e)
            }
            return false
        }
        val oldGate = gate.clone().also { it.name = args[1]}
        MessageUtil.sendWithLog(sender, Message.COMMAND_RENAME_SUCCESS, MessageUtil.getGateInfo(oldGate, sender), MessageUtil.getGateInfo(gate, sender))
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

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

object PGUnlink {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.unlink")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        try {
            dbUtil.unlink(sender.uniqueId, args[1])
        } catch (e: Exception) {
            when (e) {
                is DBUtil.GateNotLinkedException -> MessageUtil.send(sender, Message.COMMAND_UNLINK_ERROR_NOT_LINKED)
                is DBUtil.GateNotFoundException -> MessageUtil.send(sender, Message.ERROR_GATE_NOT_FOUND)
                else -> MessageUtil.catchUnexpectedError(sender, e)
            }
            return false
        }
        MessageUtil.send(sender, Message.COMMAND_UNLINK_SUCCESS, args[1])
        MessageUtil.logInfo(Message.COMMAND_UNLINK_SUCCESS_LOG, sender.name, args[1])
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

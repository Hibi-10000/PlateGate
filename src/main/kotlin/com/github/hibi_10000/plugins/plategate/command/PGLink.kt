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

object PGLink {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.link")) return false
        if (args.size != 3) return Util.commandInvalid(sender, label)

        try {
            dbUtil.link(sender.uniqueId, args[1], sender.uniqueId, args[2])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) MessageUtil.sendErrorMessage(sender, "ゲートが見つかりませんでした")
            else MessageUtil.catchUnexpectedError(sender, e)
            return false
        }
        MessageUtil.send(sender, Message.COMMANDS_LINK_SUCCESS, args[1], args[2])
        MessageUtil.logInfo(Message.COMMANDS_LINK_SUCCESS_LOG, sender.name, args[1], args[2])
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

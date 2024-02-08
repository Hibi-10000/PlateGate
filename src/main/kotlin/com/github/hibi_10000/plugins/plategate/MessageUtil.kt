/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.logging.Level

object MessageUtil {
    fun sendMessage(sender: CommandSender?, color: ChatColor?, message: String) {
        sender?.sendMessage("${ChatColor.GREEN}[PlateGate] $color$message")
    }

    fun sendMessage(sender: CommandSender?, message: String) {
        sendMessage(sender, ChatColor.AQUA, message)
    }

    fun sendErrorMessage(sender: CommandSender?, message: String) {
        sendMessage(sender, ChatColor.RED, message)
    }

    fun catchUnexpectedError(sender: CommandSender?, throwable: Throwable) {
        sendErrorMessage(sender, "予期せぬエラーが発生しました")
        instance.logger.log(Level.SEVERE, "予期せぬエラーが発生しました", throwable)
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGRename {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.rename")) return false
        if (args.size != 3) return util.commandInvalid(sender, label)

        try {
            dbUtil.rename(sender.uniqueId, args[1], args[2])
        } catch (e: Exception) {
            when (e) {
                is DBUtil.GateNameDuplicateException -> sender.sendMessage("§a[PlateGate]§c \"${args[2]}\"は既に使用されています。")
                is DBUtil.GateNotFoundException -> sender.sendMessage("§a[PlateGate]§c ゲートが見つかりませんでした")
                else -> sender.sendMessage("§a[PlateGate]§c 予期せぬエラーが発生しました。")
            }
            return false
        }
        sender.sendMessage("§a[PlateGate] §bPlateGate ${args[1]} を ${args[2]} にリネームしました")
        println("§a[PlateGate] §b${sender.name} がPlateGate ${args[1]} を ${args[2]} にリネームしました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGRemove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.remove")) return false
        if (!(args.size == 2 || args.size == 3 && args[2].equals("force", ignoreCase = true)))
            return util.commandInvalid(sender, label)
        val p = sender as Player

        if (!dbUtil.isDuplicateName(args[1], null)) {
            sender.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
            return false
        }
        val index = dbUtil.firstIndexJson("name", args[1], sender) ?: return false
        val name = dbUtil.getJson(index, "name", sender)!!
        val owner = util.getOfflinePlayer(dbUtil.getJson(index, "owner", sender)!!, null)
        if (owner.uniqueId.toString() != p.uniqueId.toString()) {
            if (!p.hasPermission("plategate.admin")) {
                p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。")
                return false
            }
            if (args.size == 3) {
                if (!args[2].equals("force", ignoreCase = true)) {
                    p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。")
                    p.sendMessage("                       §b強制的に削除する場合はコマンドの末尾に \" force\" を付けてください。")
                    return false
                }
            }
        }
        val oldLocBlock = dbUtil.gateLocation(index, sender).block
        val oldLocUnderBlock = util.underBlock(oldLocBlock)
        oldLocBlock.type = Material.AIR
        oldLocUnderBlock.type = dbUtil.underBlock(index, sender)
        dbUtil.removeJson(index, sender)
        if (args.size == 3) {
            if (args[2].equals("force", ignoreCase = true)) {
                p.sendMessage("§a[PlateGate] §bGate:${name}(Owner:${owner.name}) を強制的に削除しました。")
                println("§a[PlateGate] §b${p.name} が Gate:${name}(Owner:${owner.name}) を§c強制的に§b削除しました。")
                return true
            }
            return util.commandInvalid(sender, label)
        }
        p.sendMessage("§a[PlateGate] §bGate:${name} を削除しました。")
        println("§a[PlateGate] §b${p.name} が Gate:${name} を削除しました。")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

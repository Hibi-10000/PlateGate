/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.*
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object PGMove {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (!Util.checkPermission(sender, "plategate.command.move")) return false
        if (args.size != 2) return Util.commandInvalid(sender, label)

        if (sender.isFlying) {
            MessageUtil.sendErrorMessage(sender, "地面に立っている必要があります")
            return false
        }
        val loc = sender.location.clone()
        loc.pitch = 0f
        if (loc.y != loc.block.y.toDouble()) {
            MessageUtil.sendErrorMessage(sender, "下のブロックはフルブロックである必要があります")
            return false
        }
        val underBlock = Util.underBlock(loc.block)
        if (underBlock.type.hasGravity()) {
            MessageUtil.sendErrorMessage(sender, "下のブロックは重力の影響を受けないブロックである必要があります")
            return false
        }
        if (underBlock.type.isInteractable
            || (underBlock.type.data != Material.AIR.data && underBlock.type.data != Material.GRASS_BLOCK.data)
        ) {
            MessageUtil.sendErrorMessage(sender, "下のブロックはデータ値を持たないブロックである必要があります")
            return false
        }
        if (!underBlock.type.isOccluding && !underBlock.type.isSolid
            && Material.entries.filter { it.name.contains("GLASS") }.none { it == underBlock.type }
        ) {
            MessageUtil.sendErrorMessage(sender, "下のブロックは非透過ブロックかガラスである必要があります")
            return false
        }
        if (loc.block.type != Material.AIR) {
            MessageUtil.sendErrorMessage(sender, "その場所の非フルブロックを取り除いてください")
            return false
        }

        val oldGate: CraftPlateGate
        try {
            oldGate = dbUtil.get(sender.uniqueId, args[1])
        } catch (e: Exception) {
            if (e is DBUtil.GateNotFoundException) MessageUtil.sendErrorMessage(sender, "ゲートが見つかりませんでした")
            else MessageUtil.sendErrorMessage(sender, "予期せぬエラーが発生しました")
            return false
        }
        val oldBlock = oldGate.getBlock()
        if (oldBlock == null) {
            MessageUtil.sendErrorMessage(sender, "ワールドが見つかりませんでした")
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
            if (e is DBUtil.GateLocationDuplicateException) MessageUtil.sendErrorMessage(sender, "その場所は他のゲートと干渉します")
            else MessageUtil.sendErrorMessage(sender, "予期せぬエラーが発生しました")
            return false
        }
        val oldUnderBlock = Util.underBlock(oldBlock)
        oldBlock.type = Material.AIR
        oldUnderBlock.type = oldGate.beforeBlock
        Util.noInteract(sender.uniqueId)
        loc.block.type = Material.STONE_PRESSURE_PLATE
        underBlock.type = Material.IRON_BLOCK
        MessageUtil.sendMessage(sender, "ゲート ${args[1]} を $loc に移動しました")
        instance.logger.info("${sender.name} がゲート ${args[1]} を $loc に移動しました")
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

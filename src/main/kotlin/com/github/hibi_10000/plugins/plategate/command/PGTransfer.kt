/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.instance
import com.github.hibi_10000.plugins.plategate.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Entity
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object PGTransfer {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3) return util.commandInvalid(sender, label)

        val gateName = args[1]
        when (args[2].lowercase(Locale.ROOT)) {
            "accept" -> {
                //TODO: 新しい所有者か確認
                if (sender.hasMetadata("plategate_NewOwner")) {
                    //TODO: /plategate transfer <name> accept 許可したときの処理
                }
                return false
            }

            "reject" -> {
                //TODO: 新しい所有者か確認
                //TODO: /plategate transfer <name> reject 拒否したときの処理
                return false
            }
            "owner" -> {
                if (!util.checkPermission(sender, "plategate.command.transfer")) return false
                if (args.size != 4) return util.commandInvalid(sender, label)

                val gate: CraftPlateGate
                try {
                    gate = dbUtil.get(sender.uniqueId, gateName)
                } catch (e: Exception) {
                    if (e is DBUtil.GateNotFoundException) sender.sendMessage("§a[PlateGate] §cゲートが見つかりませんでした")
                    else sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました")
                    return false
                }
                val world = gate.getWorld()
                if (world == null) {
                    sender.sendMessage("§a[PlateGate] §cワールドが見つかりませんでした")
                    return false
                }
                val newOwner = util.getPlayer(args[3], sender) ?: return false
                //TODO: MetadataValueに何を入れるか
                newOwner.setMetadata("plategate_NewOwner", FixedMetadataValue(instance, ""))
                //TODO: いい感じに色を付ける
                val playerInfo = TextComponent(sender.name)
                playerInfo.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_ENTITY, Entity(
                        "minecraft:player", sender.uniqueId.toString(), TextComponent(sender.name)
                    )
                )
                val gateInfo = TextComponent(gate.name)
                gateInfo.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, Text(
                        "Name: ${gate.name
                        }\nOwner: ${sender.name
                        }\nWorld: ${world.name
                        }\nX: ${gate.x
                        }\nY: ${gate.y
                        }\nZ: ${gate.z
                        }\nRotate: ${gate.rotate.name
                        }\nTo: ${gate.toName ?: "null"} (Owner: ${gate.toOwner?.let { util.getOfflinePlayer(it, null) }?.name ?: "null"})"
                    )
                )
                newOwner.spigot().sendMessage(
                    TextComponent("§a[PlateGate]§b "), playerInfo,
                    TextComponent(" §bがあなたにゲート "), gateInfo,
                    TextComponent(" §bの所有権を譲渡しようとしています。")
                )
                val accept = TextComponent("§a[受け入れる]§r")
                accept.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで要求を受け入れる"))
                accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} accept")
                val reject = TextComponent("§c[拒否する]§r")
                reject.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§cクリックで要求を拒否する"))
                reject.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} reject")
                newOwner.spigot().sendMessage(TextComponent("            "), accept, TextComponent(" | "), reject)
                sender.sendMessage("§a[PlateGate] §b${newOwner.name} にゲート ${gate.name} の所有権を譲渡しようとしています。")
                println("[PlateGate] §b${sender.name} が ${newOwner.name} にゲート ${gate.name} の所有権を譲渡しようとしています。")
                return true
            }
            else -> return util.commandInvalid(sender, label)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        if (args.size <= 2) return null
        if (args.size == 3) {
            return listOf("owner")
        } else if (args[2].lowercase(Locale.ROOT) == "owner") {
            return Bukkit.getOnlinePlayers().map { it.name }
        }
        return null
    }
}

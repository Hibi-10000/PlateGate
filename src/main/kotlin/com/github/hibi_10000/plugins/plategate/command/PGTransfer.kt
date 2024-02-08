/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.*
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Entity
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object PGTransfer {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: Player, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3) return Util.commandInvalid(sender, label)

        val gateName = args[1]
        when (args[2].lowercase(Locale.ROOT)) {
            "accept" -> {
                if (args.size != 3) return Util.commandInvalid(sender, label)
                //新しい所有者か確認
                val gate = transfer[sender.uniqueId]
                if (gate?.name != gateName) return false
                //TODO: /plategate transfer <name> accept 受け入れたときの処理
                return true
            }
            "reject" -> {
                if (args.size != 3) return Util.commandInvalid(sender, label)
                //新しい所有者か確認
                val gate = transfer[sender.uniqueId]
                if (gate?.name != gateName) return false
                //TODO: /plategate transfer <name> reject 拒否したときの処理
                return false
            }
            "cancel" -> {
                if (args.size != 3) return Util.commandInvalid(sender, label)
                val entry = transfer.entries.find { it.value.name == gateName && it.value.owner == sender.uniqueId }
                if (entry == null) {
                    sender.sendMessage("§a[PlateGate] §c譲渡要求が存在しないか、削除されています")
                    return false
                }
                val gate = entry.value
                val np = Util.getPlayer(entry.key, sender) ?: return false
                transfer.remove(np.uniqueId)
                np.sendMessage("§a[PlateGate] §cゲート ${gate.name} の所有権譲渡要求がキャンセルされました")
                MessageUtil.sendMessage(sender, "${np.name} へのゲート ${gate.name} の所有権譲渡要求をキャンセルしました")
                instance.logger.info("${sender.name} が ${np.name} へのゲート ${gate.name} の所有権譲渡要求をキャンセルしました")
                return true
            }
            "owner" -> {
                if (!Util.checkPermission(sender, "plategate.command.transfer")) return false
                if (args.size != 4) return Util.commandInvalid(sender, label)

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
                val newOwner = Util.getPlayer(args[3], sender) ?: return false
                transfer[newOwner.uniqueId] = gate
                //TODO: いい感じに色を付ける
                val senderInfo = TextComponent(sender.name)
                senderInfo.hoverEvent = HoverEvent(
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
                        }\nTo: ${gate.toName ?: "null"} (Owner: ${gate.toOwner?.let { Util.getOfflinePlayer(it, null) }?.name ?: "null"})"
                    )
                )
                newOwner.spigot().sendMessage(
                    TextComponent("§a[PlateGate]§b "), senderInfo,
                    TextComponent(" §bがあなたにゲート "), gateInfo,
                    TextComponent(" §bの所有権を譲渡しようとしています")
                )
                val accept = TextComponent("§a[受け入れる]§r")
                accept.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで要求を受け入れる"))
                accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} accept")
                val reject = TextComponent("§c[拒否する]§r")
                reject.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§cクリックで要求を拒否する"))
                reject.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} reject")
                newOwner.spigot().sendMessage(TextComponent("            "), accept, TextComponent(" | "), reject)
                val newOwnerInfo = TextComponent(newOwner.name)
                newOwnerInfo.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_ENTITY, Entity(
                        "minecraft:player", newOwner.uniqueId.toString(), TextComponent(newOwner.name)
                    )
                )
                val cancel = TextComponent("§c[キャンセルする]§r")
                cancel.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§cクリックで譲渡をキャンセルする"))
                cancel.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer ${gate.name} cancel")
                sender.spigot().sendMessage(
                    TextComponent("§a[PlateGate]§b "), newOwnerInfo,
                    TextComponent(" §bにゲート "), gateInfo,
                    TextComponent(" §bの所有権を譲渡しようとしています "), cancel
                )
                instance.logger.info("${sender.name} が ${newOwner.name} にゲート ${gate.name} の所有権を譲渡しようとしています")
                return true
            }
            else -> return Util.commandInvalid(sender, label)
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

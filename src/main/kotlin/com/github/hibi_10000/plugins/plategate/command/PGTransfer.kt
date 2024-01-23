/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.instance
import com.github.hibi_10000.plugins.plategate.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

class PGTransfer {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3) return util.commandInvalid(sender, label)

        val gateName = args[1]
        sender as Player
        when (args[2].lowercase()) {
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
                if (args.size < 4 || 5 < args.size) return util.commandInvalid(sender, label)

                //val newOwnerName = args[3]
                val gateIndex = dbUtil.firstIndexJson("name", gateName, sender) ?: return false
                val oldOwner = util.getOfflinePlayer(UUID.fromString(dbUtil.getJson(gateIndex, "owner", sender)), sender)!!
                if (args.size == 5) {
                    if (args[4].equals("force", ignoreCase = true)) {
                        //強制的にownerを変更
                        if (!util.checkPermission(sender, "plategate.admin")) return false
                        if (!dbUtil.gateExists(null, gateName, sender)) return false
                        val newOwner = util.getPlayer(args[3], sender) ?: return false
                        dbUtil.setJson(gateIndex, "owner", newOwner.uniqueId.toString(), sender)
                        sender.sendMessage("§a[PlateGate] §bゲート $gateName のオーナーを ${oldOwner.name} から ${newOwner.name} に変更しました")
                        println("§a[PlateGate] §bゲート $gateName のオーナーを ${oldOwner.name} から ${newOwner.name} に変更しました")
                        return true
                    }
                    return util.commandInvalid(sender, label)
                }
                //senderのPlateGateかどうか確認
                if (oldOwner.uniqueId != sender.uniqueId) {
                    sender.sendMessage("§a[PlateGate] §cそれはあなたのゲートではありません！")
                    return false
                }
                if (!dbUtil.gateExists(null, gateName, sender)) return false
                val newOwner = util.getPlayer(args[3], sender) ?: return false
                //TODO: MetadataValueに何を入れるか
                newOwner.setMetadata("plategate_NewOwner", FixedMetadataValue(instance, ""))
                //TODO: いい感じに色を付ける
                val gateInfo = TextComponent(gateName)
                gateInfo.hoverEvent = HoverEvent(
                    HoverEvent.Action.SHOW_TEXT, Text(
                        "Name: ${gateName
                        }\nOwner: ${sender.name
                        }\nWorld: ${dbUtil.getJson(gateIndex, "world", sender)
                        }\nX: ${dbUtil.getJson(gateIndex, "x", sender)
                        }\nY: ${dbUtil.getJson(gateIndex, "y", sender)
                        }\nZ: ${dbUtil.getJson(gateIndex, "z", sender)
                        }\nRotate: ${dbUtil.getJson(gateIndex, "rotate", sender)
                        }\nTo: ${dbUtil.getJson(gateIndex, "to", sender)}"
                    )
                )
                newOwner.spigot().sendMessage(
                    TextComponent("§a[PlateGate] §b${sender.name} があなたにゲート "), gateInfo,
                    TextComponent(" の所有権を譲渡しようとしています。")
                )
                println("[PlateGate] §b${sender.name} が $newOwner にゲート $gateName の所有権を譲渡しようとしています。")
                val accept = TextComponent("§a[受け入れる]§r")
                accept.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで要求を受け入れる"))
                accept.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer $gateName accept")
                val reject = TextComponent("§c[拒否する]§r")
                reject.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§cクリックで要求を拒否する"))
                reject.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label transfer $gateName reject")
                newOwner.spigot().sendMessage(TextComponent("            "), accept, TextComponent(" | "), reject)
                return true
            }
            else -> return util.commandInvalid(sender, label)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val list: MutableList<String> = ArrayList()
        if (args.size <= 2) return list
        if (args.size == 3) {
            list.add("owner")
            list.add("accept")
            return list
        } else if (args[2].equals("owner", ignoreCase = true)) {
            for (p in Bukkit.getOnlinePlayers()) {
                if (args[3].equals(p.name, ignoreCase = true) && p.hasPermission("plategate.admin")) {
                    list.clear()
                    list.add("force")
                    return list
                }
                list.add(p.name)
            }
            return list
        }
        list.clear()
        return list
    }
}

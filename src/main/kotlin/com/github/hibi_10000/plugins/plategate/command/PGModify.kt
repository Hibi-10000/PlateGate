/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import com.github.hibi_10000.plugins.plategate.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class PGModify {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size <= 2) return util.commandInvalid(sender, label)

        when (args[2].lowercase()) {
            "accept" -> {
                //TODO: 新しい所有者か確認 → /pg modify <name> accept 許可したときの処理
                return false
            }
            "owner" -> {
                if (!util.checkPermission(sender, "plategate.command.modify")) return false
                if (args.size <= 3) {
                    sender.sendMessage("")
                    return false
                }
                if (args.size > 5) {
                    sender.sendMessage("")
                    return false
                }
                val index = dbUtil.firstIndexJson("name", args[1], sender as Player) ?: return false
                val oldOwner = Bukkit.getOfflinePlayer(UUID.fromString(dbUtil.getJson(index, "owner", sender)))
                if (args.size == 5) {
                    if (args[4].equals("force", ignoreCase = true)) {
                        if (!util.checkPermission(sender, "plategate.admin")) return false
                        /*
                        JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
                        if (jo.get("name").getAsString().equalsIgnoreCase("null")) {
                            sender.sendMessage("§[PlateGate] §cゲート名が間違っています");
                            return false;
                        }
                        Player gateOldOwner = Bukkit.getPlayer(UUID.fromString(jo.get("owner").getAsString()));
                        */
                        //val gateOldOwner: Player?
                        if (!dbUtil.gateExists(null, args[1], sender)) return false
                        //val index = util.firstIndexJson("name", args[1], sender)
                        //gateOldOwner = Bukkit.getPlayer(UUID.fromString(util.getJson(index, "owner", sender)))
                        for (lp in Bukkit.getOnlinePlayers()) {
                            if (lp.name.equals(args[3], ignoreCase = true)) {
                                val newOwner = Bukkit.getPlayer(args[3])
                                if (newOwner == null) {
                                    sender.sendMessage("§a[PlateGate] §cプレイヤーが存在しません")
                                    return false
                                }

                                dbUtil.setJson(index, "owner", newOwner.uniqueId.toString(), sender)
                                sender.sendMessage("§a[PlateGate] §bゲート ${args[1]} のオーナーを ${oldOwner.name} から ${newOwner.name} に変更しました")
                                println("§a[PlateGate] §bゲート ${args[1]} のオーナーを ${oldOwner.name} から ${newOwner.name} に変更しました")
                                return true
                            }
                        }
                        sender.sendMessage(" ${args[3]} ")
                        return false
                    }
                    sender.sendMessage("")
                    return false
                }
                if (UUID.fromString(dbUtil.getJson(index,"owner",sender)) != sender.uniqueId) {
                    //senderのPlateGateかどうか確認
                    sender.sendMessage("")
                    return false
                }
                if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[3]))) {
                    if (!dbUtil.gateExists(null, args[1], sender)) return false
                    val ttNewOwner = Bukkit.getPlayer(args[3])
                    val ttGateName = args[1]
                    ttNewOwner!!.sendMessage("")
                    val toNewOwner = TextComponent("[] 受け入れる")
                    toNewOwner.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("クリックで要求を受け入れる"))
                    toNewOwner.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label modify $ttGateName accept")
                    ttNewOwner.spigot().sendMessage(toNewOwner)
                    return true

                }
                sender.sendMessage(" ${args[3]} ")
                return false
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

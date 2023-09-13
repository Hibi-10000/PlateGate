package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGList {

    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("plategate.command.list")) {
            sender.sendMessage("§a[PlateGate] §c権限が不足しています。")
            return false
        }
        if (!(args.size == 2 || args.size == 1)) {
            val help =
                TextComponent("§a[PlateGate] §cコマンドが間違っています。 /$label help で使用法を確認してください。")
            help.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで§b\"/$label help\"§aを実行"))
            help.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help")
            sender.spigot().sendMessage(help)
            return false
        }
        val searchp: Player?
        if (sender.hasPermission("plategate.admin") && args.size == 2) {
            var args1player = false
            for (p in Bukkit.getOnlinePlayers()) {
                if (p.name.equals(args[1], ignoreCase = true)) {
                    args1player = true
                    break
                }
            }
            for (p in Bukkit.getOfflinePlayers()) {
                if (p.name.equals(args[1], ignoreCase = true)) {
                    args1player = true
                    break
                }
            }
            if (!args1player) {
                sender.sendMessage("§a[PlateGate] §cそのプレイヤーは存在しません。")
                return false
            }
            searchp = Bukkit.getPlayer(args[1])
        } else {
            searchp = sender as Player
        }

        //List<JsonObject> jolist = new JsonHandler(plugin).JsonRead(searchp, null);
        for (index in util.IndexJson("owner", searchp!!.uniqueId.toString(), (sender as Player))) {
            sender.sendMessage("§a[PlateGate] §bPlayer §6" + searchp.name + " §bが所有しているGate一覧")
            if (util.getJson(index, "to", sender).equals("", ignoreCase = true)) {
                sender.sendMessage(" §b" + util.getJson(index, "name", sender))
                //} else if (new JsonHandler(plugin).JsonRead(jog.get("to").getAsString(), null).getAsJsonObject()
                //		.get("to").getAsString() == jog.get("name").getAsString()) {
                //	sender.sendMessage(" " + jog.get("name").getAsString() + " <--> " + jog.get("to").getAsString());
            } else {
                sender.sendMessage(
                    " §b" + util.getJson(index, "name", sender) + " §a---> §b" + util.getJson(
                        index, "to", sender
                    )
                )
            }
        }
        return true
    }

    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

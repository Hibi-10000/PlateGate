package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util.util
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGLink {

    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("plategate.link")) {
            sender.sendMessage("§a[PlateGate] §c権限が不足しています。")
            return false
        }
        if (args.size != 3) {
            val help =
                TextComponent("§a[PlateGate] §cコマンドが間違っています。 /$label help で使用法を確認してください。")
            help.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text("§aクリックで§b\"/$label help\"§aを実行")
            )
            help.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help")
            sender.spigot().sendMessage(help)
            return false
        }

        //new JsonHandler(plugin).JsonChange(args[1], null, null, args[2], null, null, null);
        util.setJson(util.firstIndexJson("name", args[1], (sender as Player)), "to", args[2], sender)
        sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " から ゲート " + args[2] + " の方向にゲートをリンクしました。")
        println("§a[PlateGate] §bゲート " + args[1] + " から ゲート " + args[2] + " の方向にゲートをリンクしました。")
        return true
    }

    fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

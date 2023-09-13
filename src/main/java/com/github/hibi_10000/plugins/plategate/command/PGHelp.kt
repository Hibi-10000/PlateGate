package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.version
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PGHelp {
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("plategate.help")) {
            sender.sendMessage("§a[PlateGate] §c権限が不足しています。")
            return false
        }
        if (!(args.size == 2 || args.size == 1)) {
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
        if (args.size == 2) {
            if (args[1].equals("plugin", ignoreCase = true)) {
                sender.sendMessage("§a[PlateGate $version] §6MoreHelp")
                sender.sendMessage("Version: §b$version")
                sender.sendMessage("Author: Hibi_10000")
                sender.sendMessage("GitHub: https://github.com/Hibi-10000/PlateGate")
                sender.sendMessage("BukkitDev: https://dev.bukkit.org/projects/plategate")
                return true
            }
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
        sender.sendMessage("§a[PlateGate $version] §6Help")
        sender.sendMessage(" §6Command§r:")
        val help1 = TextComponent(" - §b/$label create [GateName]")
        help1.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label create\"§aをチャットにセット")
        )
        help1.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label create ")
        sender.spigot().sendMessage(help1)
        val help2 = TextComponent(" - §b/$label move [GateName]")
        help2.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label move\"§aをチャットにセット")
        )
        help2.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label move ")
        sender.spigot().sendMessage(help2)
        val help3 = TextComponent(" - §b/$label delete [GateName]")
        help3.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label delete\"§aをチャットにセット")
        )
        help3.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label delete ")
        sender.spigot().sendMessage(help3)
        val help4 = TextComponent(" - §b/$label link [GateName:ここから] [GateName:ここへ飛ぶ]")
        help4.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label link\"§aをチャットにセット")
        )
        help4.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label link ")
        sender.spigot().sendMessage(help4)

        /*
		TextComponent help5 = new TextComponent(" - §b/" + label +" modify [TargetGateName] [Object:<name/owner(OPOnly)>] [値:<NewGateName/Player>]");
		help5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aクリックで§b\"/" + label + " modify\"§aをチャットにセット")));
		help5.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + label + " modify "));
		sender.spigot().sendMessage(help5);
		*/
        val help6 = TextComponent(" - §b/$label list [Player(OPOnly)]")
        help6.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label list\"§aをチャットにセット")
        )
        help6.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label list ")
        sender.spigot().sendMessage(help6)
        val help7 = TextComponent(" - §b/$label help")
        help7.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label help\"§aを実行")
        )
        help7.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help")
        sender.spigot().sendMessage(help7)
        val help8 = TextComponent(" - §b/$label jump [GateName]")
        help8.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label jump\"§aをチャットにセット")
        )
        help8.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label jump ")
        sender.spigot().sendMessage(help8)

        /*
		sender.sendMessage(" §6Permission:§r");
		
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		sender.sendMessage(" - §bplategate.");
		*/
        sender.sendMessage("")
        val morehelp = TextComponent(" §aHelpPlugin")
        morehelp.hoverEvent = HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            Text("§aクリックで§b\"/$label help plugin\"§aを実行")
        )
        morehelp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help plugin")
        sender.spigot().sendMessage(morehelp)
        return true
    }

    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

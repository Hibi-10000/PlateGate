/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util
import com.github.hibi_10000.plugins.plategate.version
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PGHelp {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!util.checkPermission(sender, "plategate.command.help")) return false
        if (args.size == 2) {
            if (args[1].equals("plugin", ignoreCase = true)) {
                sender.sendMessage("§a[PlateGate $version] §6MoreHelp")
                sender.sendMessage("Version: §b$version")
                sender.sendMessage("Author: Hibi_10000")
                sender.sendMessage("GitHub: https://github.com/Hibi-10000/PlateGate")
                sender.sendMessage("BukkitDev: https://dev.bukkit.org/projects/plategate")
                sender.sendMessage("License: Mozilla Public License 2.0")
                return true
            }
        }
        if (args.size != 1) return util.commandInvalid(sender, label)
        sender.sendMessage("§a[PlateGate $version] §6Help")
        sender.sendMessage(" §6Command§r:")

        sender.spigot().sendMessage(commandHelp(label, "create", "[GateName]"))
        sender.spigot().sendMessage(commandHelp(label,   "move", "[GateName]"))
        sender.spigot().sendMessage(commandHelp(label, "delete", "[GateName]"))
        sender.spigot().sendMessage(commandHelp(label,   "link", "[GateName:ここから] [GateName:ここへ飛ぶ]"))
        sender.spigot().sendMessage(commandHelp(label, "modify", "[TargetGateName] [Object:<name/owner(OPOnly)>] [値:<NewGateName/Player>]"))
        sender.spigot().sendMessage(commandHelp(label,   "list", "[Player(OPOnly)]"))
        sender.spigot().sendMessage(commandHelp(label,   "jump", "[GateName]"))

        val help = TextComponent(" - §b/$label help")
        help.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで§b\"/$label help\"§aを実行"))
        help.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help")
        sender.spigot().sendMessage(help)

        /*
        sender.sendMessage("")
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
        val moreHelp = TextComponent(" §aHelpPlugin")
        moreHelp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで§b\"/$label help plugin\"§aを実行"))
        moreHelp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label help plugin")
        sender.spigot().sendMessage(moreHelp)
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }

    private fun commandHelp(label: String, command: String, options: String): TextComponent {
        val help = TextComponent(" - §b/$label $command $options")
        help.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§aクリックで§b\"/$label $command\"§aをチャットにセット"))
        help.clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/$label $command")
        return help
    }
}

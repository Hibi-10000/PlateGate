/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class PGBase : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!command.name.equals("plategate", ignoreCase = true)) return false
        if (!util.checkPermission(sender, "plategate.command")) return false
        if (args.isEmpty()) return util.commandInvalid(sender, label)

        if (args[0].equals("create", ignoreCase = true)) {
            return PGCreate().onCommand(sender, command, label, args)
        } else if (args[0].equals("delete", ignoreCase = true)) {
            return PGDelete().onCommand(sender, command, label, args)
        } else if (args[0].equals("help", ignoreCase = true)) {
            return PGHelp().onCommand(sender, command, label, args)
        } else if (args[0].equals("jump", ignoreCase = true)) {
            return PGJump().onCommand(sender, command, label, args)
        } else if (args[0].equals("link", ignoreCase = true)) {
            return PGLink().onCommand(sender, command, label, args)
        } else if (args[0].equals("list", ignoreCase = true)) {
            return PGList().onCommand(sender, command, label, args)
        } else if (args[0].equals("modify", ignoreCase = true)) {
            return PGModify().onCommand(sender, command, label, args)
        } else if (args[0].equals("move", ignoreCase = true)) {
            return PGMove().onCommand(sender, command, label, args)
        } //else if (args[0].equals("test", ignoreCase = true)) {return true}
        return util.commandInvalid(sender, label)
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        if (!command.name.equals("plategate", ignoreCase = true)) return null
        val list: MutableList<String> = ArrayList()
        if (args[0].equals("create", ignoreCase = true)) {
            return PGCreate().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("move", ignoreCase = true)) {
            return PGMove().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("delete", ignoreCase = true)) {
            return PGDelete().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("link", ignoreCase = true)) {
            return PGLink().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("modify", ignoreCase = true)) {
            return PGModify().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("list", ignoreCase = true)) {
            return PGList().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("help", ignoreCase = true)) {
            return PGHelp().onTabComplete(sender, command, alias, args)
        } else if (args[0].equals("jump", ignoreCase = true)) {
            return PGJump().onTabComplete(sender, command, alias, args)
        } else if (args.size == 1) {
            list.add("create")
            list.add("move")
            list.add("delete")
            list.add("link")
            list.add("modify")
            list.add("list")
            list.add("help")
            list.add("jump")
            return list
        }
        return null
    }
}

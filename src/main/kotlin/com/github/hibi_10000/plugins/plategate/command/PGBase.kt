/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.util
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PGBase : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!command.name.equals("plategate", ignoreCase = true)) return false
        if (!util.checkPermission(sender, "plategate.command")) return false
        //if (args[0].lowercase() == "test") {return true}
        if (sender !is Player) return false
        if (args.isEmpty()) return util.commandInvalid(sender, label)

        return when (args[0].lowercase()) {
            "create"   -> PGCreate  ().onCommand(sender, command, label, args)
            "help"     -> PGHelp    ().onCommand(sender, command, label, args)
            "jump"     -> PGJump    ().onCommand(sender, command, label, args)
            "link"     -> PGLink    ().onCommand(sender, command, label, args)
            "list"     -> PGList    ().onCommand(sender, command, label, args)
            "move"     -> PGMove    ().onCommand(sender, command, label, args)
            "remove"   -> PGRemove  ().onCommand(sender, command, label, args)
            "rename"   -> PGRename  ().onCommand(sender, command, label, args)
            "transfer" -> PGTransfer().onCommand(sender, command, label, args)
            //"test"   -> {true}
            else -> util.commandInvalid(sender, label)
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        if (!command.name.equals("plategate", ignoreCase = true)) return null
        if (!sender.hasPermission("plategate.command")) return null
        if (sender !is Player) return null

        return when (args[0].lowercase()) {
            "create"   -> PGCreate  ().onTabComplete(sender, command, alias, args)
            "help"     -> PGHelp    ().onTabComplete(sender, command, alias, args)
            "jump"     -> PGJump    ().onTabComplete(sender, command, alias, args)
            "link"     -> PGLink    ().onTabComplete(sender, command, alias, args)
            "list"     -> PGList    ().onTabComplete(sender, command, alias, args)
            "move"     -> PGMove    ().onTabComplete(sender, command, alias, args)
            "remove"   -> PGRemove  ().onTabComplete(sender, command, alias, args)
            "rename"   -> PGRename  ().onTabComplete(sender, command, alias, args)
            "transfer" -> PGTransfer().onTabComplete(sender, command, alias, args)
            else -> {
                if (args.size == 1) {
                    arrayListOf("create", "help", "jump", "link", "list", "move", "remove", "rename", "transfer")
                } else {
                    null
                }
            }
        }
    }
}

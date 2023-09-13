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
import java.util.*

class PGModify {
    private var oldowner: Player? = null

    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (checkPermission(sender, "plategate.command.modify")) return false
        if (args.size <= 2) return commandInvalid(sender, label)

        if (args[2].equals("name", ignoreCase = true)) {
            if (args.size <= 3) {
                sender.sendMessage("")
                return false
            }

            //new JsonHandler(plugin).JsonChange(args[1], args[3], null, null, null, null, null);
            if (!util.gateExists(null, args[1], (sender as Player))) return false
            val index = util.firstIndexJson("name", args[1], sender)
            util.setJson(index, "name", args[3], sender)
            sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " の名前を " + args[3] + " に変更しました")
            println("§a[PlateGate] §bゲート " + args[1] + " の名前を " + args[3] + " に変更しました")
            return true
        } else if (args[2].equals("owner", ignoreCase = true)) {
            if (args.size <= 3) {
                sender.sendMessage("")
                return false
            }
            if (args.size > 5) {
                sender.sendMessage("")
                return false
            }
            if (args.size == 5) {
                if (args[4].equals("force", ignoreCase = true)) {
                    /*
					JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
					if (jo.get("name").getAsString().equalsIgnoreCase("null")) {
						sender.sendMessage("§[PlateGate] §cゲート名が間違っています");
						return false;
					}
					Player gateoldowner = Bukkit.getPlayer(UUID.fromString(jo.get("owner").getAsString()));
					*/
                    //val gateoldowner: Player?
                    if (!util.gateExists(null, args[1], (sender as Player))) return false
                    //val index = util.firstIndexJson("name", args[1], sender)
                    //gateoldowner = Bukkit.getPlayer(UUID.fromString(util.getJson(index, "owner", sender)))
                    for (lp in Bukkit.getOnlinePlayers()) {
                        if (lp.name.equals(args[3], ignoreCase = true)) {
                            val newOwner = Bukkit.getPlayer(args[3])
                            if (newOwner == null) {
                                sender.sendMessage("§a[PlateGate] §cプレイヤーが存在しません")
                                return false
                            }

                            //new JsonHandler(plugin).JsonChange(args[1], null, Bukkit.getPlayer(args[3]), null, null, null, null);
                            util.setJson(
                                util.firstIndexJson("name", args[1], sender),
                                "owner",
                                newOwner.uniqueId.toString(),
                                sender
                            )
                            sender.sendMessage("§a[PlateGate] §bゲート " + args[1] + " のオーナーを " + oldowner!!.name + " から " + newOwner.name + " に変更しました")
                            println("§a[PlateGate] §bゲート " + args[1] + " のオーナーを " + oldowner!!.name + " から " + newOwner.name + " に変更しました")
                            return true
                        }
                    }
                    sender.sendMessage("" + args[3] + "")
                    return false
                }
                sender.sendMessage("")
                return false
            }
            if (!Bukkit.getPlayer(
                    UUID.fromString(
                        util.getJson(
                            util.firstIndexJson("name", args[1], (sender as Player)),"owner",sender
                        )
                    )
                )?.name.equals(sender.getName(), ignoreCase = true)
            ) {
                sender.sendMessage("")
                return false
            }
            if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[3]))) {
                //JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
                if (!util.gateExists(null, args[1], sender)) return false
                val index = util.firstIndexJson("name", args[1], sender)
                if (Bukkit.getOnlinePlayers()
                        .contains(Bukkit.getPlayer(UUID.fromString(util.getJson(index, "owner", sender))))
                ) {
                    oldowner = sender
                    val ttop = Bukkit.getPlayer(args[3])
                    val ttopgn = args[1]
                    ttop!!.sendMessage("")
                    val tonewowner = TextComponent("[] 受け入れる")
                    tonewowner.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("クリックで要求を受け入れる"))
                    tonewowner.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/$label modify $ttopgn accept")
                    ttop.spigot().sendMessage(tonewowner)
                    return true
                }
            }
            sender.sendMessage("" + args[3] + "")
            return false
        } else if (args[2].equals("accept", ignoreCase = true)) {
            if (oldowner!!.name.equals(sender.name, ignoreCase = true)) {
                //TODO: ここは何だ？
            }
        }
        return false
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val list: MutableList<String> = ArrayList()
        if (args.size <= 2) return list
        if (args.size == 3) {
            list.add("name")
            list.add("owner")
            list.add("accept")
            return list
        } else if (args[2].equals("owner", ignoreCase = true)) {
            for (p in Bukkit.getOnlinePlayers()) {
                if (args[3].equals(p.name, ignoreCase = true) && (p.isOp || p.hasPermission("          "))) {
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

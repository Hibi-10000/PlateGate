/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.command

import com.github.hibi_10000.plugins.plategate.dbUtil
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PGDelete {
    @Suppress("UNUSED_PARAMETER")
    fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!checkPermission(sender, "plategate.command.delete")) return false
        if (!(args.size == 2 || args.size == 3 && args[2].equals("force", ignoreCase = true)))
            return commandInvalid(sender, label)
        val p = sender as Player

        /*
		JsonObject jo = new JsonHandler(plugin).JsonRead(args[1], null).getAsJsonObject();
		if (jo.get("name").getAsString().equalsIgnoreCase("null")) {}

		if (!(Bukkit.getPlayer(jo.get("owner").getAsString()) == p)) {
			if (!(p.hasPermission("plategate.admin"))) {
				p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。");
				return false;
			}
			if (args.length == 3) {
				if (!(args[2].equalsIgnoreCase("force"))) {
					p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。");
					p.sendMessage("                       §b強制的に削除する場合はコマンドの末尾に \" force\" を付けてください。");
					return false;
				}
			}
		}

		Location oldloc = new Location(Bukkit.getWorld(jo.get("world").getAsString()), Double.parseDouble(jo.get("x").getAsString()),
				Double.parseDouble(jo.get("y").getAsString()), Double.parseDouble(jo.get("z").getAsString()));
		Location olddownloc = new Location(p.getWorld(), oldloc.getBlockX(), oldloc.getBlockY() - 1, oldloc.getBlockZ());

		oldloc.getBlock().setType(Material.AIR);
		olddownloc.getBlock().setType(Material.getMaterial(jo.get("beforeblock").getAsString()));

		new JsonHandler(plugin).JsonRemove(args[1]);
		//plugin.arrayJson.remove(plugin.arrayJson.firstIndexOf("name", args[1]));
		if ((args[2].equalsIgnoreCase("force"))) {
			p.sendMessage("§a[PlateGate] §bGate:" + jo.get("name").getAsString() + "(Owner:" + jo.get("Owner").getAsString() + ")を強制的に削除しました。");
			System.out.println("§a[PlateGate] §b" + p.getName() + " がGate:" + jo.get("name").getAsString() + "(Owner:" + jo.get("Owner").getAsString() + ")を強制的に削除しました。");
		} else {
			p.sendMessage("§a[PlateGate] §bGate:" + jo.get("name").getAsString() + "を削除しました。");
			System.out.println("§a[PlateGate] §b" + p.getName() + " がGate:" + jo.get("name").getAsString() + "を削除しました。");
		}
		*/
        val index = dbUtil.firstIndexJson("name", args[1], sender)
        if (dbUtil.getJson(index, "name", sender).equals("null", ignoreCase = true)) {
            //TODO: ここは何だ?
        }
        if (Bukkit.getPlayer(dbUtil.getJson(index, "owner", sender)) !== p) {
            if (!p.hasPermission("plategate.admin")) {
                p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。")
                return false
            }
            if (args.size == 3) {
                if (!args[2].equals("force", ignoreCase = true)) {
                    p.sendMessage("§a[PlateGate] §cそれはあなたのPlateGateではありません。")
                    p.sendMessage("                       §b強制的に削除する場合はコマンドの末尾に \" force\" を付けてください。")
                    return false
                }
            }
        }
        val oldloc = Location(
            Bukkit.getWorld(dbUtil.getJson(index, "world", sender)),
            dbUtil.getJson(index, "x", sender).toDouble(),
            dbUtil.getJson(index, "y", sender).toDouble(),
            dbUtil.getJson(index, "z", sender).toDouble()
        )
        val olddownloc =
            Location(p.world, oldloc.blockX.toDouble(), (oldloc.blockY - 1).toDouble(), oldloc.blockZ.toDouble())
        oldloc.block.type = Material.AIR
        olddownloc.block.type = Material.getMaterial(dbUtil.getJson(index, "beforeblock", sender))!!
        dbUtil.removeJson(dbUtil.firstIndexJson("name", args[1], sender), sender)
        if (args[2].equals("force", ignoreCase = true)) {
            p.sendMessage(
                "§a[PlateGate] §bGate:" + dbUtil.getJson(
                    index,
                    "name",
                    sender
                ) + "(Owner:" + dbUtil.getJson(index, "Owner", sender) + ")を強制的に削除しました。"
            )
            println(
                "§a[PlateGate] §b" + p.name + " がGate:" + dbUtil.getJson(
                    index,
                    "name",
                    sender
                ) + "(Owner:" + dbUtil.getJson(index, "Owner", sender) + ")を強制的に削除しました。"
            )
        } else {
            p.sendMessage("§a[PlateGate] §bGate:" + dbUtil.getJson(index, "name", sender) + "を削除しました。")
            println("§a[PlateGate] §b" + p.name + " がGate:" + dbUtil.getJson(index, "name", sender) + "を削除しました。")
        }
        return true
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        return null
    }
}

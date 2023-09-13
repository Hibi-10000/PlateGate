package com.github.hibi_10000.plugins.plategate.util;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class Util {
	PlateGate plugin;
	public 	Util(PlateGate insetance) {plugin = insetance;}

	public boolean gateExists(String id, String name, Player sender) {
		try {
			if (id != null) {
				if (id.equalsIgnoreCase("0")) return false;
                return plugin.arrayJson.get(id, "name") != null;
            } else if (name != null) {
				if (plugin.arrayJson.firstIndexOf("name", name).equalsIgnoreCase("0")) {
					sender.sendMessage("§a[PlateGate] §cゲート名が間違っています");
					return false;
				}
				return true;
			}
			return false;
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			return false;
		}
	}

	public String getJson(String id, String name, Player sender) {
		try {
			return plugin.arrayJson.get(id, name);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public void addJson(Player sender, String... vaules) {
		try {
			plugin.arrayJson.add(vaules);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public void setJson(String id, String key, String vaule, Player sender) {
		try {
			plugin.arrayJson.set(id, key, vaule);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public void removeJson(String id, Player sender) {
		try {
			plugin.arrayJson.remove(id);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public String firstIndexJson(String key, String vaule, Player sender) {
		try {
			return plugin.arrayJson.firstIndexOf(key, vaule);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public String lastIndexJson(String key, String value, Player sender) {
		try {
			return plugin.arrayJson.lastIndexOf(key, value);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public List<String> IndexJson(String key, String value, Player sender) {
		try {
			return plugin.arrayJson.IndexOf(key, value);
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public void clearJson(Player sender) {
		try {
			plugin.arrayJson.clear();
		} catch (IOException e) {
			sender.sendMessage("§a[PlateGate] §c予期せぬエラーが発生しました");
			throw new RuntimeException(e);
		}
	}

	public String IndexJson(Location loc, Player p) {
		List<String> xIndexList = IndexJson("x", String.valueOf(loc.getBlockX()), p);
		//List<String> yIndex = util.IndexJson("y", String.valueOf(loc.getBlockY()), p);
		//List<String> zIndex = util.IndexJson("z", String.valueOf(loc.getBlockZ()), p);
		//List<String> worldIndex = util.IndexJson("world", String.valueOf(loc.getWorld()), p);
		//String index = "0";
		for (String xIndex : xIndexList) {
			//boolean x = util.getJson(xIndex, "x", p).equalsIgnoreCase(String.valueOf(loc.getBlockX()));
			boolean y = getJson(xIndex, "y", p).equalsIgnoreCase(String.valueOf(loc.getBlockY()));
			boolean z = getJson(xIndex, "z", p).equalsIgnoreCase(String.valueOf(loc.getBlockZ()));
			boolean w = getJson(xIndex, "world", p).equalsIgnoreCase(String.valueOf(loc.getWorld()));
			//if (y && z && w) index = xIndex;
			if (y && z && w) return xIndex;
		}
		return "0";
	}
}

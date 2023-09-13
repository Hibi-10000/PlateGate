package com.github.hibi_10000.plugins.plategate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.hibi_10000.plugins.plategate.util.ArrayJson;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.hibi_10000.plugins.plategate.command.PGBase;
import com.github.hibi_10000.plugins.plategate.event.Event;

public class PlateGate extends JavaPlugin {
	
	public String ver = getDescription().getVersion();
	
	public File gatelistjsonfile = new File(getDataFolder(), "gatelist.json");

	public ArrayJson arrayJson;

	@Override
	public void onEnable() {
		if (!gatelistjsonfile.exists()) {
			saveResource("gatelist.json", false);
		}
		getCommand("pg").setExecutor(new PGBase(this));
		getCommand("pg").setTabCompleter(new PGBase(this));
		getServer().getPluginManager().registerEvents(new Event(this), this);
		List<String> namelist = new ArrayList<>();
		namelist.add("name");
		namelist.add("owner");
		namelist.add("to");
		namelist.add("x");
		namelist.add("y");
		namelist.add("z");
		namelist.add("rotate");
		namelist.add("world");
		namelist.add("beforeblock");
		arrayJson = new ArrayJson(this, namelist);
	}
}
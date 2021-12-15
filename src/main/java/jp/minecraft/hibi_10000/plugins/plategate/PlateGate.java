package jp.minecraft.hibi_10000.plugins.plategate;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import jp.minecraft.hibi_10000.plugins.plategate.command.PGBase;
import jp.minecraft.hibi_10000.plugins.plategate.event.Event;

public class PlateGate extends JavaPlugin {
	
	public String ver = "v1.0.0";
	
	File gatelistjsonfile = new File(getDataFolder(), "gatelist.json");
	//public PlateGate plugin = this;
	
	
	@Override
	public void onEnable() {
		if (!gatelistjsonfile.exists()) {
			saveResource("gatelist.json", false);
		}
		getCommand("pg").setExecutor(new PGBase(this));
		getCommand("pg").setTabCompleter(new PGBase(this));
		getServer().getPluginManager().registerEvents(new Event(this), this);
	}
	
	@Override
	public void onDisable() {
		
	}
}
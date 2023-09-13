package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.command.PGBase
import com.github.hibi_10000.plugins.plategate.event.Event
import com.github.hibi_10000.plugins.plategate.util.ArrayJson
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

var instance: PlateGate? = null
var version: String? = null
var arrayJson: ArrayJson? = null

class PlateGate : JavaPlugin() {
    override fun onEnable() {
        instance = this
        version = description.version
        val gateDB = File(dataFolder, "gate.json")
        if (!gateDB.exists()) {
            saveResource("gate.json", false)
        }
        arrayJson = ArrayJson(gateDB)
        getCommand("pg")?.setExecutor(PGBase())
        getCommand("pg")?.tabCompleter = PGBase()
        server.pluginManager.registerEvents(Event(), this)
    }
}
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.github.hibi_10000.plugins.plategate.command.PGBase
import com.github.hibi_10000.plugins.plategate.database.DBUtil
import com.github.hibi_10000.plugins.plategate.database.JsonUtil
import com.github.hibi_10000.plugins.plategate.event.Event
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

internal lateinit var instance: Main
internal lateinit var dbUtil: DBUtil
internal val noInteract: MutableList<UUID> = mutableListOf()

class Main : JavaPlugin() {
    override fun onEnable() {
        instance = this
        val gateDB = File(dataFolder, "gate.json")
        if (!gateDB.exists()) {
            saveResource("gate.json", false)
        }
        dbUtil = JsonUtil(gateDB)
        getCommand("plategate")?.setExecutor(PGBase)
        getCommand("plategate")?.tabCompleter = PGBase
        server.pluginManager.registerEvents(Event, this)
    }
}

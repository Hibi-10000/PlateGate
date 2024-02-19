/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.hibi_10000.plugins.plategate.localization

import org.bukkit.entity.Player

enum class Message(val jsonKey: String) {
    //<editor-fold desc="Messages">
    COMMANDS_CREATE_SUCCESS("commands.create.success"),
    COMMANDS_CREATE_SUCCESS_LOG("commands.create.success.log");
    //</editor-fold>

    private fun format(base: String, vararg format: String): String {
        return if (format.isEmpty()) base else base.format(*format)
    }

    fun getString(vararg format: String): String {
        return format(Language.getMessage(this), *format)
    }

    fun getString(player: Player?, vararg format: String): String {
        if (player == null) return getString(*format)
        return format(Language.fromString(player.locale).getMessage(this), *format)
    }
}

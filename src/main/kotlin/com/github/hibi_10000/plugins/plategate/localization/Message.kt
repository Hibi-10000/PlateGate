/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.hibi_10000.plugins.plategate.localization

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class Message(val jsonKey: String) {
    //<editor-fold desc="Messages">
    COMMAND_CREATE_SUCCESS("commands.plategate.create.success"),
    COMMAND_CREATE_SUCCESS_LOG("commands.plategate.create.success.log"),
    COMMAND_JUMP_SUCCESS("commands.plategate.jump.success"),
    COMMAND_JUMP_SUCCESS_LOG("commands.plategate.jump.success.log"),
    COMMAND_LINK_SUCCESS("commands.plategate.link.success"),
    COMMAND_LINK_SUCCESS_LOG("commands.plategate.link.success.log"),
    COMMAND_MOVE_SUCCESS("commands.plategate.move.success"),
    COMMAND_MOVE_SUCCESS_LOG("commands.plategate.move.success.log"),
    COMMAND_REMOVE_SUCCESS("commands.plategate.remove.success"),
    COMMAND_REMOVE_SUCCESS_LOG("commands.plategate.remove.success.log"),
    ERROR_PERMISSION("plategate.permission_error"),
    ERROR_UNEXPECTED("plategate.unexpected_error"),
    GATE_BLOCK_BREAK_ERROR("plategate.block.break.error");
    //</editor-fold>

    private fun format(base: String, vararg format: String): String {
        return if (format.isEmpty()) base else base.format(*format)
    }

    fun getString(vararg format: String): String {
        return format(Language.getMessage(this), *format)
    }

    fun getString(sender: CommandSender?, vararg format: String): String {
        if (sender == null || sender !is Player) return getString(*format)
        return format(Language.fromString(sender.locale).getMessage(this), *format)
    }
}

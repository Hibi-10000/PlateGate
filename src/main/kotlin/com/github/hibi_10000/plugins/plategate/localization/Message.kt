/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.localization

import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class Message(val jsonKey: String, val color: ChatColor? = null) {
    //<editor-fold desc="Messages">
    COMMAND_CREATE_SUCCESS("commands.plategate.create.success", ChatColor.AQUA),
    COMMAND_CREATE_SUCCESS_LOG("commands.plategate.create.success.log"),
    COMMAND_JUMP_SUCCESS("commands.plategate.jump.success", ChatColor.AQUA),
    COMMAND_JUMP_SUCCESS_LOG("commands.plategate.jump.success.log"),
    COMMAND_LINK_SUCCESS("commands.plategate.link.success", ChatColor.AQUA),
    COMMAND_LINK_SUCCESS_LOG("commands.plategate.link.success.log"),
    COMMAND_LIST_HEADER("commands.plategate.list.header", ChatColor.AQUA),
    COMMAND_MOVE_SUCCESS("commands.plategate.move.success", ChatColor.AQUA),
    COMMAND_MOVE_SUCCESS_LOG("commands.plategate.move.success.log"),
    COMMAND_REMOVE_SUCCESS("commands.plategate.remove.success", ChatColor.AQUA),
    COMMAND_REMOVE_SUCCESS_LOG("commands.plategate.remove.success.log"),
    COMMAND_RENAME_SUCCESS("commands.plategate.rename.success", ChatColor.AQUA),
    COMMAND_RENAME_SUCCESS_LOG("commands.plategate.rename.success.log"),
    COMMAND_TRANSFER_CANCEL_ERROR_NOT_FOUND("commands.plategate.transfer.cancel.error.not_found", ChatColor.RED),
    COMMAND_TRANSFER_CANCEL_SUCCESS("commands.plategate.transfer.cancel.success", ChatColor.AQUA),
    COMMAND_TRANSFER_CANCEL_SUCCESS_NOTICE("commands.plategate.transfer.cancel.success.notice", ChatColor.RED),
    COMMAND_TRANSFER_CANCEL_SUCCESS_LOG("commands.plategate.transfer.cancel.success.log"),
    COMMAND_TRANSFER_ERROR_TARGET_QUIT("commands.plategate.transfer.error.target_quit", ChatColor.RED),
    COMMAND_TRANSFER_REQUEST_SUCCESS("commands.plategate.transfer.request.success", ChatColor.AQUA),
    COMMAND_TRANSFER_REQUEST_SUCCESS_NOTICE("commands.plategate.transfer.request.success.notice", ChatColor.AQUA),
    COMMAND_TRANSFER_REQUEST_SUCCESS_LOG("commands.plategate.transfer.request.success.log"),
    COMMAND_UNLINK_ERROR_NOT_LINKED("commands.plategate.unlink.error.not_linked", ChatColor.RED),
    COMMAND_UNLINK_SUCCESS("commands.plategate.unlink.success", ChatColor.AQUA),
    COMMAND_UNLINK_SUCCESS_LOG("commands.plategate.unlink.success.log"),
    ERROR_BREAK_GATE_BLOCK("plategate.error.break.block", ChatColor.RED),
    ERROR_GATE_LOCATION_INTERFERENCE("plategate.error.gate.location.interference", ChatColor.RED),
    ERROR_GATE_NAME_ALREADY_USED("plategate.error.gate.name.already_used", ChatColor.RED),
    ERROR_GATE_NOT_FOUND("plategate.error.gate.not_found", ChatColor.RED),
    ERROR_PERMISSION("plategate.error.permission", ChatColor.RED),
    ERROR_PLAYER_NOT_FOUND("plategate.error.player.not_found", ChatColor.RED),
    ERROR_PLAYER_OFFLINE("plategate.error.player.offline", ChatColor.RED),
    ERROR_UNEXPECTED("plategate.error.unexpected", ChatColor.RED),
    ERROR_WORLD_NOT_FOUND("plategate.error.world.not_found", ChatColor.RED),
    ;
    //</editor-fold>

    fun getString(): String {
        return Language.getMessage(this)
    }

    fun getString(sender: CommandSender?): String {
        if (sender == null || sender !is Player) return getString()
        return Language.fromString(sender.locale).getMessage(this)
    }
}

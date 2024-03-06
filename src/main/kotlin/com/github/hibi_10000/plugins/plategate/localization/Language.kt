/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.localization

import com.github.hibi_10000.plugins.plategate.instance
import com.google.gson.Gson
import com.google.gson.JsonObject

enum class Language(private val key: String) {
    //<editor-fold desc="Languages">
    EN_US("en_us"),
    JA_JP("ja_jp");
    //</editor-fold>

    private val jo: JsonObject

    init {
        val json = instance.getResource("lang/${this.key}.json")?.use {
            it.readAllBytes().decodeToString()
        }
        jo = Gson().fromJson(json, JsonObject::class.java)
    }

    private val message = { key: Message -> jo[key.jsonKey]?.asString }

    fun getMessage(key: Message): String {
        return message(key) ?: Language.getMessage(key)
    }

    companion object {
        fun fromString(lang: String): Language {
            return entries.firstOrNull { it.key == lang } ?: EN_US
        }

        fun getMessage(key: Message): String {
            return EN_US.message(key) ?: JA_JP.message(key)!!
        }
    }
}

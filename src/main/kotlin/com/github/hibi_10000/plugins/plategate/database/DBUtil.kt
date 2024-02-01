/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import java.io.File

abstract class DBUtil(@Suppress("UNUSED_PARAMETER") gateDB: File) {
    abstract fun add(plateGate: CraftPlateGate)
    abstract fun get(name: String, owner: String): CraftPlateGate
    abstract fun get(world: String, x: Int, y: Int, z: Int): CraftPlateGate
    abstract fun getList(owner: String): List<CraftPlateGate>
    abstract fun link(name: String, owner: String, to: String)
    abstract fun move(plateGate: CraftPlateGate)
    abstract fun remove(name: String, owner: String)
    abstract fun rename(name: String, owner: String, newName: String)
    abstract fun transfer(name: String, owner: String, newOwner: String)
}

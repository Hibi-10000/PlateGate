/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import java.io.File
import java.io.IOException
import java.util.*

abstract class DBUtil(@Suppress("UNUSED_PARAMETER") gateDB: File) {
    /**
     * Add a new gate to the database
     * @param plateGate The gate to add
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun add(plateGate: CraftPlateGate)
    /**
     * Get [CraftPlateGate] by name and owner
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to get
     * @return [CraftPlateGate] corresponding to the input values
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun get(owner: UUID, name: String): CraftPlateGate
    /**
     * Get [CraftPlateGate] by coordinates
     * @param world World-specific [UUID]
     * @param x X-axis of coordinates
     * @param y Y-axis of coordinates
     * @param z Z-axis of coordinates
     * @return [CraftPlateGate] corresponding to the input values
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun get(world: UUID, x: Int, y: Int, z: Int): CraftPlateGate
    /**
     * Get a list of [CraftPlateGate] owned by the input owner
     * @param owner Player-specific [UUID] of the gate owner
     * @return List of [CraftPlateGate] owned by the input owner
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun getList(owner: UUID): List<CraftPlateGate>
    /**
     * Link the gate to another gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to link
     * @param toOwner Player-specific [UUID] of the gate owner to link to
     * @param toName The name of the gate to link to
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun link(owner: UUID, name: String, toOwner: UUID, toName: String)
    /**
     * Move the gate to a new location
     * @param plateGate [CraftPlateGate] to move
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun move(plateGate: CraftPlateGate)
    /**
     * Remove the gate from the database
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to remove
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun remove(owner: UUID, name: String)
    /**
     * Rename the gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to rename
     * @param newName The new name of the gate
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun rename(owner: UUID, name: String, newName: String)
    /**
     * Transfer the gate to the new owner
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to transfer
     * @param newOwner Player-specific [UUID] of the new gate owner
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun transfer(owner: UUID, name: String, newOwner: UUID)
    /**
     * Unlink the gate from another gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to unlink
     */
    @Throws(IOException::class, RuntimeException::class)
    abstract fun unlink(owner: UUID, name: String)

    class GateLocationDuplicateException: RuntimeException()
    class GateNameDuplicateException: RuntimeException()
    class GateNotFoundException: RuntimeException()
    class GateNotLinkedException: RuntimeException()
}

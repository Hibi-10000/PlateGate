/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate.database

import com.github.hibi_10000.plugins.plategate.CraftPlateGate
import java.io.IOException
import java.util.*

interface DBUtil {
    /**
     * Add a new gate to the database
     * @param plateGate The gate to add
     */
    @Throws(IOException::class, RuntimeException::class)
    fun add(plateGate: CraftPlateGate)
    /**
     * Get [CraftPlateGate] by name and owner
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to get
     * @return [CraftPlateGate] corresponding to the input values
     */
    @Throws(IOException::class, RuntimeException::class)
    fun get(owner: UUID, name: String): CraftPlateGate
    /**
     * Get [CraftPlateGate] by coordinates
     * @param world World-specific [UUID]
     * @param x X-axis of coordinates
     * @param y Y-axis of coordinates
     * @param z Z-axis of coordinates
     * @return [CraftPlateGate] corresponding to the input values
     */
    @Throws(IOException::class, RuntimeException::class)
    fun get(world: UUID, x: Int, y: Int, z: Int): CraftPlateGate
    /**
     * Get a list of [CraftPlateGate] owned by the input owner
     * @param owner Player-specific [UUID] of the gate owner
     * @return List of [CraftPlateGate] owned by the input owner
     */
    @Throws(IOException::class, RuntimeException::class)
    fun getList(owner: UUID): List<CraftPlateGate>
    /**
     * Link the gate to another gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to link
     * @param toOwner Player-specific [UUID] of the gate owner to link to
     * @param toName The name of the gate to link to
     * @return List of [CraftPlateGate] linked
     */
    @Throws(IOException::class, RuntimeException::class)
    fun link(owner: UUID, name: String, toOwner: UUID, toName: String): List<CraftPlateGate>
    /**
     * Move the gate to a new location
     * @param plateGate [CraftPlateGate] to move
     */
    @Throws(IOException::class, RuntimeException::class)
    fun move(plateGate: CraftPlateGate)
    /**
     * Remove the gate from the database
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to remove
     */
    @Throws(IOException::class, RuntimeException::class)
    fun remove(owner: UUID, name: String)
    /**
     * Rename the gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to rename
     * @param newName The new name of the gate
     * @return [CraftPlateGate] with the new name
     */
    @Throws(IOException::class, RuntimeException::class)
    fun rename(owner: UUID, name: String, newName: String): CraftPlateGate
    /**
     * Unlink the gate from another gate
     * @param owner Player-specific [UUID] of the gate owner
     * @param name The name of the gate to unlink
     * @return List of [CraftPlateGate] before being unlinked
     */
    @Throws(IOException::class, RuntimeException::class)
    fun unlink(owner: UUID, name: String): List<CraftPlateGate?>

    class GateAlreadyLinkedException: RuntimeException()
    class GateLocationDuplicateException: RuntimeException()
    class GateNameDuplicateException: RuntimeException()
    class GateNotFoundException: RuntimeException()
    class GateNotLinkedException: RuntimeException()
}

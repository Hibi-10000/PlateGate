/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.github.hibi_10000.plugins.plategate

import com.google.gson.JsonObject
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import java.util.*

class CraftPlateGate(
    var owner: UUID,
    var name: String,
    world: UUID,
    x: Int,
    y: Int,
    z: Int,
    rotate: BlockFace,
    beforeBlock: Material,
    var to: String?
) {
    var world: UUID = world
        private set
    var x: Int = x
        private set
    var y: Int = y
        private set
    var z: Int = z
        private set
    var rotate: BlockFace = rotate
        private set
    var beforeBlock: Material = beforeBlock
        private set

    constructor(owner: UUID, name: String, block: Block, rotate: BlockFace, to: String?) : this(
        owner,
        name,
        block.world.uid,
        block.x,
        block.y,
        block.z,
        rotate,
        block.getRelative(BlockFace.DOWN).type,
        to
    )

    constructor(jo: JsonObject) : this(
        UUID.fromString(jo["owner"].asString),
        jo["name"].asString,
        UUID.fromString(jo["world"].asString),
        jo["x"].asInt,
        jo["y"].asInt,
        jo["z"].asInt,
        BlockFace.valueOf(jo["rotate"].asString),
        Material.getMaterial(jo["beforeBlock"].asString)!!,
        jo["to"]?.asString
    )

    override fun toString(): String {
        return "CraftPlateGate{owner=$owner, name=$name, world=$world, x=$x, y=$y, z=$z, rotate=$rotate, beforeBlock=$beforeBlock, to=$to}"
    }
}

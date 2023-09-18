package com.github.hibi_10000.plugins.plategate

import org.bukkit.block.BlockFace

class Util {
    fun convBlockFace2Facing(blockFace: BlockFace): String {
        return when (blockFace) {
            BlockFace.SOUTH -> "south" /* yaw >= 315 || yaw <=  45 */
            BlockFace.WEST  -> "west"  /* yaw >   45 && yaw <  135 */
            BlockFace.NORTH -> "north" /* yaw >= 135 && yaw <= 225 */
            BlockFace.EAST  -> "east"  /* yaw >  225 && yaw <  315 */
            else            -> "south"
        }
    }

    fun convFacing2Yaw(facing: String): Float {
        return when (facing.lowercase()) {
            "south" ->   0f
            "west"  ->  90f
            "north" -> 180f
            "east"  -> 270f
            else    ->   0f
        }
    }
}
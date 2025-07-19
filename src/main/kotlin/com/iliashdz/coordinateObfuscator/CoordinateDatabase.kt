package com.iliashdz.coordinateObfuscator

import org.bukkit.entity.Player

object CoordinateDatabase {
    private var offsets = HashMap<String, CoordinateOffset>()

    fun isObfuscateEnabledForPlayer(player: Player): Boolean {
        return true
    }

    fun fetchPlayerOffset(player: Player): CoordinateOffset {
        val uid = player.uniqueId
        var ret: CoordinateOffset
        if (offsets[player.name] != null)
            ret = offsets[player.name] as CoordinateOffset
        else {
            ret = CoordinateOffset(0, 0)
            if (isObfuscateEnabledForPlayer(player))
                ret = CoordinateOffset.randomize()
            CoordinateObfuscator.logger.info(
                "Coordinate offset of player " + player.name + " set to [" + (ret.x * CoordinateOffset.COORDINATE_SCALE) + ", " + (ret.z * CoordinateOffset.COORDINATE_SCALE) + "]")
            offsets[player.name] = ret
        }
        return ret
    }

    fun removePlayerOffset(player: Player) {
        offsets.remove(player.name)
    }
}
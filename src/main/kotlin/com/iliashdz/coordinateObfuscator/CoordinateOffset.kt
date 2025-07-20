package com.iliashdz.coordinateObfuscator

import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.ChunkCoordIntPair
import java.util.Random

class CoordinateOffset(val x: Int, val z: Int) {
    companion object {
        const val COORDINATE_SCALE = 256
        const val OFFSET_LIMIT_FACTOR = 1024
        val random = Random()

        fun randomize(): CoordinateOffset {
            return CoordinateOffset(
                random.nextInt(-OFFSET_LIMIT_FACTOR, OFFSET_LIMIT_FACTOR),
                random.nextInt(-OFFSET_LIMIT_FACTOR, OFFSET_LIMIT_FACTOR)
            )
        }
    }

    fun getChunkX(): Int {
        return x * (COORDINATE_SCALE / 16)
    }

    fun getChunkZ(): Int {
        return z * (COORDINATE_SCALE / 16)
    }

    fun mask(x: Double, y: Double, z: Double): Triple<Double, Double, Double> {
        return Triple(
            x + this.x * COORDINATE_SCALE,
            y,
            z + this.z * COORDINATE_SCALE
        )
    }

    fun unmask(x: Double, y: Double, z: Double): Triple<Double, Double, Double> {
        return Triple(
            x - this.x * COORDINATE_SCALE,
            y,
            z - this.z * COORDINATE_SCALE
        )
    }

    fun maskPosition(p: BlockPosition): BlockPosition {
        return BlockPosition(
            p.x + this.x * COORDINATE_SCALE,
            p.y,
            p.z + this.z * COORDINATE_SCALE
        )
    }

    fun unmaskPosition(p: BlockPosition): BlockPosition {
        return BlockPosition(
            p.x - this.x * COORDINATE_SCALE,
            p.y,
            p.z - this.z * COORDINATE_SCALE
        )
    }

    fun maskChunk(v: ChunkCoordIntPair): ChunkCoordIntPair {
        return ChunkCoordIntPair(
            v.chunkX + getChunkX(),
            v.chunkZ + getChunkZ()
        )
    }

    fun unmaskChunk(v: ChunkCoordIntPair): ChunkCoordIntPair {
        return ChunkCoordIntPair(
            v.chunkX - getChunkX(),
            v.chunkZ - getChunkZ()
        )
    }
}
package com.iliashdz.coordinateObfuscator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.*
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.ChunkCoordIntPair
import org.bukkit.block.Block
import org.bukkit.entity.Player

class PacketTransformer(params: AdapterParameteters) : PacketAdapter(params) {
    override fun onPacketSending(event: PacketEvent?) {
        if (event == null) return
        if (!handleClientBound(event.packet, event.player))
            event.isCancelled = true
    }

    override fun onPacketReceiving(event: PacketEvent?) {
        if (event == null) return
        handleServerBound(event.packet, event.player)
    }

    fun handleClientBound(packet: PacketContainer, player: Player): Boolean {
        val offset = CoordinateDatabase.fetchPlayerOffset(player)

        maskAllPositions(packet, offset)
        maskAllChunkCoords(packet, offset)

        // TODO: Fix map data!!!
        // if (packet.type.isClient)
        //    CoordinateObfuscator.logger.info(packet.type.name())

        when (packet.type) {
            PacketType.Play.Server.SPAWN_ENTITY -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.ENTITY_TELEPORT -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.EXPLOSION -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.INITIALIZE_BORDER -> maskDouble2(packet, offset, 0)
            PacketType.Play.Server.WORLD_PARTICLES -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.VEHICLE_MOVE -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.LOOK_AT -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.SPAWN_POSITION -> {}
            PacketType.Play.Server.SET_BORDER_CENTER -> maskDouble2(packet, offset, 0)
            PacketType.Play.Server.VIEW_CENTRE -> maskChunkCoordsVarInt(packet, offset, 0)
            PacketType.Play.Server.POSITION -> maskDouble3(packet, offset, 0)
            PacketType.Play.Server.MULTI_BLOCK_CHANGE -> maskChunkSection(packet, offset, 0)
            PacketType.Play.Server.BLOCK_CHANGE -> {}
            PacketType.Play.Server.NAMED_SOUND_EFFECT -> {
                val masked = offset.mask(
                    (packet.integers.values[0].toDouble()) / 8.0,
                    (packet.integers.values[1].toDouble()) / 8.0,
                    (packet.integers.values[2].toDouble()) / 8.0
                )

                packet.integers.modify(0) { v -> (masked.first * 8.0).toInt() }
                packet.integers.modify(1) { v -> (masked.second * 8.0).toInt() }
                packet.integers.modify(2) { v -> (masked.third * 8.0).toInt() }
            }
            PacketType.Play.Server.MAP_CHUNK -> maskChunkCoordsVarInt(packet, offset, 0)
            PacketType.Play.Server.LIGHT_UPDATE -> maskChunkCoordsVarInt(packet, offset, 0)
            else -> {}//CoordinateObfuscator.logger.info(packet.type.toString())
        }
        return true
    }

    fun handleServerBound(packet: PacketContainer, player: Player) {
        val offset = CoordinateDatabase.fetchPlayerOffset(player)

        unmaskAllPositions(packet, offset)
        unmaskAllChunkCoords(packet, offset)

        when (packet.type) {
            PacketType.Play.Client.POSITION -> unmaskDouble3(packet, offset, 0)
            PacketType.Play.Client.POSITION_LOOK -> unmaskDouble3(packet, offset, 0)
            PacketType.Play.Client.VEHICLE_MOVE -> unmaskDouble3(packet, offset, 0)
            PacketType.Play.Client.USE_ITEM_ON -> {
                if (packet.type.name() == "USE_ITEM_ON") {
                    var bhr = packet.modifier.values[0]

                    val spos   = bhr.javaClass.superclass.getDeclaredMethod("getLocation").invoke(bhr)
                    val dir    = bhr.javaClass.getDeclaredMethod("getDirection").invoke(bhr)
                    val pos    = bhr.javaClass.getDeclaredMethod("getBlockPos").invoke(bhr)
                    val miss   = bhr.javaClass.getDeclaredMethod("getType").invoke(bhr).toString() != "BLOCK"
                    val inside = bhr.javaClass.getDeclaredMethod("isInside").invoke(bhr) as Boolean

                    val bpos = offset.unmaskPosition(BlockPosition(
                        pos.javaClass.superclass.getDeclaredMethod("getX").invoke(pos) as Int,
                        pos.javaClass.superclass.getDeclaredMethod("getY").invoke(pos) as Int,
                        pos.javaClass.superclass.getDeclaredMethod("getZ").invoke(pos) as Int
                    ))

                    val postriple = offset.unmask(
                        spos.javaClass.getDeclaredMethod("x").invoke(spos) as Double,
                        spos.javaClass.getDeclaredMethod("y").invoke(spos) as Double,
                        spos.javaClass.getDeclaredMethod("z").invoke(spos) as Double
                    )

                    val nspos = spos.javaClass.getConstructor(Double::class.java, Double::class.java, Double::class.java)
                        .newInstance(postriple.first, postriple.second, postriple.third)

                    val npos = pos.javaClass.getConstructor(Int::class.java, Int::class.java, Int::class.java)
                        .newInstance(bpos.x, bpos.y, bpos.z)

                    var lbhr: Any = 0

                    if (miss) {
                        lbhr = bhr.javaClass.getDeclaredMethod("miss", nspos.javaClass, dir.javaClass, npos.javaClass)
                            .invoke(null, nspos, dir, npos)
                    } else {
                        lbhr = bhr.javaClass.getConstructor(nspos.javaClass, dir.javaClass, npos.javaClass, inside.javaClass)
                            .newInstance(nspos, dir, npos, inside)
                    }

                    val _spos = lbhr.javaClass.superclass.getDeclaredMethod("getLocation").invoke(lbhr)
                    val _pos  = lbhr.javaClass.getDeclaredMethod("getBlockPos").invoke(lbhr)

                    packet.modifier.modify(0) { v -> lbhr }
                }
            }
        }
    }

    fun maskDouble2(packet: PacketContainer, offset: CoordinateOffset, startIndex: Int) {
        val value = offset.mask(
            packet.doubles.values[0],
            0.0,
            packet.doubles.values[1],
        )

        packet.doubles.modify(startIndex + 0) { v -> value.first }
        packet.doubles.modify(startIndex + 1) { v -> value.third }
    }

    fun maskDouble3(packet: PacketContainer, offset: CoordinateOffset, startIndex: Int) {
        val value = offset.mask(
            packet.doubles.values[0],
            packet.doubles.values[1],
            packet.doubles.values[2],
        )

        packet.doubles.modify(startIndex + 0) { v -> value.first }
        packet.doubles.modify(startIndex + 1) { v -> value.second }
        packet.doubles.modify(startIndex + 2) { v -> value.third }
    }

    fun maskChunkCoordsVarInt(packet: PacketContainer, offset: CoordinateOffset, startIndex: Int) {
        val value = offset.maskChunk(ChunkCoordIntPair(
            packet.integers.values[0],
            packet.integers.values[1],
        ))

        packet.integers.modify(startIndex + 0) { v -> value.chunkX }
        packet.integers.modify(startIndex + 1) { v -> value.chunkZ }
    }

    fun maskAllChunkCoords(packet: PacketContainer, offset: CoordinateOffset) {
        for (i in 0..(packet.chunkCoordIntPairs.values.count() - 1)) {
            packet.chunkCoordIntPairs.modify(i) { p -> if (p == null) null else offset.maskChunk(p) }
        }
    }

    fun maskAllPositions(packet: PacketContainer, offset: CoordinateOffset) {
        for (i in 0..(packet.sectionPositions.values.count() - 1)) {
            val spos = packet.sectionPositions.values[i]
            val cc = offset.maskChunk(ChunkCoordIntPair(spos.x, spos.z))
            packet.sectionPositions.modify(i) { p -> BlockPosition(cc.chunkX, spos.y, cc.chunkZ) }
        }
        for (i in 0..(packet.blockPositionModifier.values.count() - 1)) {
            packet.blockPositionModifier.modify(i) { p -> if (p == null) null else offset.maskPosition(p) }
        }
    }

    fun maskChunkSection(packet: PacketContainer, offset: CoordinateOffset, index: Int) {
        var spos = packet.sectionPositions.values[index]

        val cc = offset.maskChunk(ChunkCoordIntPair(spos.x, spos.z))

        /*
        val x = num.shr(42).and(0x3FFFFF).toInt()
        val z = num.shr(20).and(0x3FFFFF).toInt()

        val cc = offset.maskChunk(ChunkCoordIntPair(x, z))

        num = cc.chunkX.toLong().and(0x3FFFFF).shl(42)
            .or(cc.chunkZ.toLong().and(0x3FFFFF).shr(20))
            .or(num.and(0x3FFFFF))
        */
        // packet.sectionPositions.modify(index) { v -> BlockPosition(cc.chunkX, spos.y, cc.chunkZ) }
    }

    fun unmaskDouble2(packet: PacketContainer, offset: CoordinateOffset, startIndex: Int) {
        val value = offset.unmask(
            packet.doubles.values[0],
            0.0,
            packet.doubles.values[1],
        )

        packet.doubles.modify(startIndex + 0) { v -> value.first }
        packet.doubles.modify(startIndex + 1) { v -> value.third }
    }

    fun unmaskDouble3(packet: PacketContainer, offset: CoordinateOffset, startIndex: Int) {
        val value = offset.unmask(
            packet.doubles.values[0],
            packet.doubles.values[1],
            packet.doubles.values[2],
        )

        packet.doubles.modify(startIndex + 0) { v -> value.first }
        packet.doubles.modify(startIndex + 1) { v -> value.second }
        packet.doubles.modify(startIndex + 2) { v -> value.third }
    }

    fun unmaskChunkCoordsVarInt(packet: PacketContainer, offset: CoordinateOffset, startIndex: Int) {
        val value = offset.unmaskChunk(ChunkCoordIntPair(
            packet.integers.values[0],
            packet.integers.values[1],
        ))

        packet.integers.modify(startIndex + 0) { v -> value.chunkX }
        packet.integers.modify(startIndex + 1) { v -> value.chunkZ }
    }

    fun unmaskAllChunkCoords(packet: PacketContainer, offset: CoordinateOffset) {
        for (i in 0..(packet.chunkCoordIntPairs.values.count() - 1)) {
            packet.chunkCoordIntPairs.modify(i) { p -> if (p == null) null else offset.unmaskChunk(p) }
        }
    }

    fun unmaskAllPositions(packet: PacketContainer, offset: CoordinateOffset) {
        for (i in 0..(packet.sectionPositions.values.count() - 1)) {
            val spos = packet.sectionPositions.values[i]
            val cc = offset.unmaskChunk(ChunkCoordIntPair(spos.x, spos.z))
            packet.sectionPositions.modify(i) { p -> BlockPosition(cc.chunkX, spos.y, cc.chunkZ) }
        }
        for (i in 0..(packet.blockPositionModifier.values.count() - 1)) {
            packet.blockPositionModifier.modify(i) { p -> if (p == null) null else offset.unmaskPosition(p) }
        }
    }

    fun unmaskChunkSection(packet: PacketContainer, offset: CoordinateOffset, index: Int) {
        var num = packet.longs.values[0]

        val x = num.shr(42).and(0x3FFFFF).toInt()
        val z = num.shr(20).and(0x3FFFFF).toInt()

        val cc = offset.unmaskChunk(ChunkCoordIntPair(x, z))

        num = cc.chunkX.toLong().and(0x3FFFFF).shl(42)
            .or(cc.chunkZ.toLong().and(0x3FFFFF).shr(20))
            .or(num.and(0x3FFFFF))
        packet.longs.modify(index) { v -> num }
    }
}
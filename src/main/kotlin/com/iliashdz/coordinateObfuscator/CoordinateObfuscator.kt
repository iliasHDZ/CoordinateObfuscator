package com.iliashdz.coordinateObfuscator

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ConnectionSide
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.injector.GamePhase
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class CoordinateObfuscator : JavaPlugin(), Listener {
    companion object {
        var logger: Logger = Logger.getGlobal()
    }

    override fun onEnable() {
        CoordinateObfuscator.logger = logger
        Bukkit.getPluginManager().registerEvents(this, this)

        val params = PacketAdapter.params()
        params.plugin(this)
        params.connectionSide(ConnectionSide.BOTH)
        params.listenerPriority(ListenerPriority.HIGHEST)
        params.gamePhase(GamePhase.PLAYING)
        params.types(
            PacketType.Play.Server.SPAWN_ENTITY,
            PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
            PacketType.Play.Server.STATISTIC,
            PacketType.Play.Server.BLOCK_CHANGED_ACK,
            PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
            PacketType.Play.Server.TILE_ENTITY_DATA,
            PacketType.Play.Server.BLOCK_ACTION,
            PacketType.Play.Server.BLOCK_CHANGE,
            PacketType.Play.Server.BOSS,
            PacketType.Play.Server.CHUNK_BATCH_FINISHED,
            PacketType.Play.Server.CHUNK_BATCH_START,
            PacketType.Play.Server.CHUNKS_BIOMES,
            PacketType.Play.Server.CLEAR_TITLES,
            PacketType.Play.Server.TAB_COMPLETE,
            PacketType.Play.Server.CLOSE_WINDOW,
            PacketType.Play.Server.WINDOW_DATA,
            PacketType.Play.Server.SET_COOLDOWN,
            PacketType.Play.Server.CUSTOM_CHAT_COMPLETIONS,
            PacketType.Play.Server.CUSTOM_PAYLOAD,
            PacketType.Play.Server.DAMAGE_EVENT,
            PacketType.Play.Server.DELETE_CHAT_MESSAGE,
            PacketType.Play.Server.KICK_DISCONNECT,
            PacketType.Play.Server.DISGUISED_CHAT,
            PacketType.Play.Server.EXPLOSION,
            PacketType.Play.Server.UNLOAD_CHUNK,
            PacketType.Play.Server.OPEN_WINDOW_HORSE,
            PacketType.Play.Server.HURT_ANIMATION,
            PacketType.Play.Server.INITIALIZE_BORDER,
            PacketType.Play.Server.KEEP_ALIVE,
            PacketType.Play.Server.MAP_CHUNK,
            PacketType.Play.Server.WORLD_EVENT,
            PacketType.Play.Server.WORLD_PARTICLES,
            PacketType.Play.Server.LIGHT_UPDATE,
            PacketType.Play.Server.MAP,
            PacketType.Play.Server.OPEN_WINDOW_MERCHANT,
            PacketType.Play.Server.VEHICLE_MOVE,
            PacketType.Play.Server.OPEN_BOOK,
            PacketType.Play.Server.OPEN_WINDOW,
            PacketType.Play.Server.OPEN_SIGN_EDITOR,
            PacketType.Play.Server.PING,
            PacketType.Play.Server.PONG_RESPONSE,
            PacketType.Play.Server.AUTO_RECIPE,
            PacketType.Play.Server.CHAT,
            PacketType.Play.Server.PLAYER_COMBAT_END,
            PacketType.Play.Server.PLAYER_COMBAT_ENTER,
            PacketType.Play.Server.PLAYER_COMBAT_KILL,
            PacketType.Play.Server.LOOK_AT,
            PacketType.Play.Server.POSITION,
            PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
            PacketType.Play.Server.RESOURCE_PACK_SEND,
            PacketType.Play.Server.RESPAWN,
            PacketType.Play.Server.MULTI_BLOCK_CHANGE,
            PacketType.Play.Server.SELECT_ADVANCEMENT_TAB,
            PacketType.Play.Server.SET_ACTION_BAR_TEXT,
            PacketType.Play.Server.SET_BORDER_CENTER,
            PacketType.Play.Server.SET_BORDER_LERP_SIZE,
            PacketType.Play.Server.SET_BORDER_SIZE,
            PacketType.Play.Server.SET_BORDER_WARNING_DELAY,
            PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE,
            PacketType.Play.Server.CAMERA,
            PacketType.Play.Server.HELD_ITEM_SLOT,
            PacketType.Play.Server.VIEW_CENTRE,
            PacketType.Play.Server.SPAWN_POSITION,
            PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE,
            PacketType.Play.Server.ATTACH_ENTITY,
            PacketType.Play.Server.SCOREBOARD_OBJECTIVE,
            PacketType.Play.Server.SCOREBOARD_TEAM,
            PacketType.Play.Server.SCOREBOARD_SCORE,
            PacketType.Play.Server.SET_SUBTITLE_TEXT,
            PacketType.Play.Server.SET_TITLE_TEXT,
            PacketType.Play.Server.SET_TITLES_ANIMATION,
            PacketType.Play.Server.ENTITY_SOUND,
            PacketType.Play.Server.NAMED_SOUND_EFFECT,
            PacketType.Play.Server.START_CONFIGURATION,
            PacketType.Play.Server.STOP_SOUND,
            PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER,
            PacketType.Play.Server.NBT_QUERY,
            PacketType.Play.Server.COLLECT,
            PacketType.Play.Server.ENTITY_TELEPORT,
            PacketType.Play.Server.ENTITY_EFFECT,
            PacketType.Play.Server.TAGS,

            PacketType.Play.Client.TILE_NBT_QUERY,
            PacketType.Play.Client.DIFFICULTY_CHANGE,
            PacketType.Play.Client.CHAT_ACK,
            PacketType.Play.Client.CHAT_COMMAND,
            PacketType.Play.Client.CHAT,
            PacketType.Play.Client.CHUNK_BATCH_RECEIVED,
            PacketType.Play.Client.CLIENT_COMMAND,
            PacketType.Play.Client.SETTINGS,
            PacketType.Play.Client.TAB_COMPLETE,
            PacketType.Play.Client.CONFIGURATION_ACK,
            PacketType.Play.Client.ENCHANT_ITEM,
            PacketType.Play.Client.WINDOW_CLICK,
            PacketType.Play.Client.CLOSE_WINDOW,
            PacketType.Play.Client.CUSTOM_PAYLOAD,
            PacketType.Play.Client.B_EDIT,
            PacketType.Play.Client.ENTITY_NBT_QUERY,
            PacketType.Play.Client.USE_ENTITY,
            PacketType.Play.Client.JIGSAW_GENERATE,
            PacketType.Play.Client.KEEP_ALIVE,
            PacketType.Play.Client.DIFFICULTY_LOCK,
            PacketType.Play.Client.POSITION,
            PacketType.Play.Client.POSITION_LOOK,
            PacketType.Play.Client.GROUND,
            PacketType.Play.Client.VEHICLE_MOVE,
            PacketType.Play.Client.BOAT_MOVE,
            PacketType.Play.Client.PICK_ITEM,
            PacketType.Play.Client.PING_REQUEST,
            PacketType.Play.Client.AUTO_RECIPE,
            PacketType.Play.Client.ABILITIES,
            PacketType.Play.Client.BLOCK_DIG,
            PacketType.Play.Client.ENTITY_ACTION,
            PacketType.Play.Client.STEER_VEHICLE,
            PacketType.Play.Client.PONG,
            PacketType.Play.Client.RECIPE_SETTINGS,
            PacketType.Play.Client.RECIPE_DISPLAYED,
            PacketType.Play.Client.ITEM_NAME,
            PacketType.Play.Client.RESOURCE_PACK_STATUS,
            PacketType.Play.Client.ADVANCEMENTS,
            PacketType.Play.Client.TR_SEL,
            PacketType.Play.Client.BEACON,
            PacketType.Play.Client.SET_COMMAND_BLOCK,
            PacketType.Play.Client.SET_COMMAND_MINECART,
            PacketType.Play.Client.SET_CREATIVE_SLOT,
            PacketType.Play.Client.SET_JIGSAW,
            PacketType.Play.Client.STRUCT,
            PacketType.Play.Client.UPDATE_SIGN,
            PacketType.Play.Client.ARM_ANIMATION,
            PacketType.Play.Client.SPECTATE,
            PacketType.Play.Client.USE_ITEM,
            PacketType.Play.Client.USE_ITEM_ON
        )

        ProtocolLibrary.getProtocolManager().addPacketListener(PacketTransformer(params))
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    fun onPlayerLeave(e: PlayerQuitEvent) {
        CoordinateDatabase.removePlayerOffset(e.player)
    }
}

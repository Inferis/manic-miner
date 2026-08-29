package org.inferis.manicminer;

import org.inferis.manicminer.events.ManicMinerEvents;
import org.inferis.manicminer.logic.VeinMinerSession;
import org.inferis.manicminer.networking.HighlightBlocksPayload;
import org.inferis.manicminer.networking.HotKeyPressedPayload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManicMiner implements ModInitializer {
	public static final String MODID = "manic-miner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ManicMinerConfig CONFIG = new ManicMinerConfig();
	public static final ArrayList<String> PLAYERS_PRESSING_HOTKEY = new ArrayList<>();

	private static final Map<String, Set<BlockPos>> lastPlayerHighlights = new HashMap<>();
	private static int tickCount = 0;

	@Override
	public void onInitialize() {
		ManicMiner.CONFIG.initialLoad();

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (player instanceof ServerPlayer serverPlayer) {
				return ManicMinerEvents.beforeBlockBreak(world, serverPlayer, pos, state);
			}
			else {
				return true;
			}
		});

		ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerLevel world) -> {
			ManicMinerEvents.onEntityLoad(entity, world);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (!CONFIG.highlightBlocks) return;
			if (++tickCount % 4 != 0) return;

			for (var player : server.getPlayerList().getPlayers()) {
				updatePlayerHighlight(player);
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			lastPlayerHighlights.remove(handler.player.getStringUUID());
		});

		registerPayloadAndReceiver();
	}

	private static void updatePlayerHighlight(ServerPlayer player) {
		var playerId = player.getStringUUID();

		if (!ManicMinerEvents.canVeinMine(player) || VeinMinerSession.sessionForPlayer(player) != null) {
			var last = lastPlayerHighlights.remove(playerId);
			if (last != null && !last.isEmpty()) {
				ServerPlayNetworking.send(player, new HighlightBlocksPayload(List.of()));
			}
			return;
		}

		var hitResult = player.pick(player.blockInteractionRange(), 0.0f, false);
		if (hitResult.getType() != HitResult.Type.BLOCK) {
			var last = lastPlayerHighlights.remove(playerId);
			if (last != null && !last.isEmpty()) {
				ServerPlayNetworking.send(player, new HighlightBlocksPayload(List.of()));
			}
			return;
		}

		var targetPos = ((BlockHitResult) hitResult).getBlockPos();
		var world = player.level();
		var newPositions = ManicMinerEvents.previewBlockMine(world, player, targetPos);

		var lastPositions = lastPlayerHighlights.getOrDefault(playerId, Set.of());
		if (!newPositions.equals(lastPositions)) {
			lastPlayerHighlights.put(playerId, new HashSet<>(newPositions));
			ServerPlayNetworking.send(player, new HighlightBlocksPayload(new ArrayList<>(newPositions)));
		}
	}

	public void registerPayloadAndReceiver() {
		PayloadTypeRegistry.serverboundPlay().register(HotKeyPressedPayload.ID, HotKeyPressedPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(HighlightBlocksPayload.ID, HighlightBlocksPayload.CODEC);

		// Register payload receiver. The client will send the payload to the server, which
		// will then in turn set the right mode on the magnet items.
		ServerPlayNetworking.registerGlobalReceiver(HotKeyPressedPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				var id = context.player().getStringUUID();
				if (payload.isPressed()) {
					if (!PLAYERS_PRESSING_HOTKEY.contains(id)) {
						PLAYERS_PRESSING_HOTKEY.add(id);
					}
				}
				else {
					PLAYERS_PRESSING_HOTKEY.remove(id);
				}
			});
		});
	}

	public static Boolean IsPlayerPressingHotKey(ServerPlayer player) {
		return PLAYERS_PRESSING_HOTKEY.contains(player.getStringUUID());
	}
}

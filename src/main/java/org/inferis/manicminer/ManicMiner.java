package org.inferis.manicminer;

import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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
			if (player instanceof ServerPlayerEntity serverPlayer) {
				return ManicMinerEvents.beforeBlockBreak(world, serverPlayer, pos, state);
			}
			else {
				return true;
			}
		});

		ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerWorld world) -> {
			ManicMinerEvents.onEntityLoad(entity, world);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (!CONFIG.highlightBlocks) return;
			if (++tickCount % 4 != 0) return;

			for (var player : server.getPlayerManager().getPlayerList()) {
				updatePlayerHighlight(player);
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			lastPlayerHighlights.remove(handler.player.getUuidAsString());
		});

		registerPayloadAndReceiver();
	}

	private static void updatePlayerHighlight(ServerPlayerEntity player) {
		var playerId = player.getUuidAsString();

		if (!ManicMinerEvents.canVeinMine(player) || VeinMinerSession.sessionForPlayer(player) != null) {
			var last = lastPlayerHighlights.remove(playerId);
			if (last != null && !last.isEmpty()) {
				ServerPlayNetworking.send(player, new HighlightBlocksPayload(List.of()));
			}
			return;
		}

		var hitResult = player.raycast(player.getBlockInteractionRange(), 0.0f, false);
		if (hitResult.getType() != HitResult.Type.BLOCK) {
			var last = lastPlayerHighlights.remove(playerId);
			if (last != null && !last.isEmpty()) {
				ServerPlayNetworking.send(player, new HighlightBlocksPayload(List.of()));
			}
			return;
		}

		var targetPos = ((BlockHitResult) hitResult).getBlockPos();
		var world = player.getEntityWorld();
		var newPositions = ManicMinerEvents.previewBlockMine(world, player, targetPos);

		var lastPositions = lastPlayerHighlights.getOrDefault(playerId, Set.of());
		if (!newPositions.equals(lastPositions)) {
			lastPlayerHighlights.put(playerId, new HashSet<>(newPositions));
			ServerPlayNetworking.send(player, new HighlightBlocksPayload(new ArrayList<>(newPositions)));
		}
	}

	public void registerPayloadAndReceiver() {
		PayloadTypeRegistry.playC2S().register(HotKeyPressedPayload.ID, HotKeyPressedPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(HighlightBlocksPayload.ID, HighlightBlocksPayload.CODEC);

		// Register payload receiver. The client will send the payload to the server, which
		// will then in turn set the right mode on the magnet items.
		ServerPlayNetworking.registerGlobalReceiver(HotKeyPressedPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				var id = context.player().getUuidAsString();
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

	public static Boolean IsPlayerPressingHotKey(ServerPlayerEntity player) {
		return PLAYERS_PRESSING_HOTKEY.contains(player.getUuidAsString());
	}
}

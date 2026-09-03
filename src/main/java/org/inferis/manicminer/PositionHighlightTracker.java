package org.inferis.manicminer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.inferis.manicminer.events.ManicMinerEvents;
import org.inferis.manicminer.logic.VeinMinerSession;
import org.inferis.manicminer.networking.HighlightBlocksPayload;

import java.util.*;

import static org.inferis.manicminer.ManicMiner.CONFIG;

public class PositionHighlightTracker {
    private final Map<String, Set<BlockPos>> playerHighlights = new HashMap<>();
    private int tickCount = 0;

    public PositionHighlightTracker() {
        PayloadTypeRegistry.clientboundPlay().register(HighlightBlocksPayload.ID, HighlightBlocksPayload.CODEC);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!CONFIG.highlightBlocks) return;
            if (++tickCount % 4 != 0) return;

            for (var player : server.getPlayerList().getPlayers()) {
                updatePlayerHighlight(player);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            removePlayerHighlight(handler.player);
        });

    }

    private void updatePlayerHighlight(ServerPlayer player) {
        var playerId = player.getStringUUID();

        if (!ManicMinerEvents.canVeinMine(player) || VeinMinerSession.sessionForPlayer(player) != null) {
            var last = playerHighlights.remove(playerId);
            if (last != null && !last.isEmpty()) {
                ServerPlayNetworking.send(player, new HighlightBlocksPayload(List.of()));
            }
            return;
        }

        var hitResult = player.pick(player.blockInteractionRange(), 0.0f, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            var last = playerHighlights.remove(playerId);
            if (last != null && !last.isEmpty()) {
                ServerPlayNetworking.send(player, new HighlightBlocksPayload(List.of()));
            }
            return;
        }

        var targetPos = ((BlockHitResult) hitResult).getBlockPos();
        var world = player.level();
        var newPositions = ManicMinerEvents.previewBlockMine(world, player, targetPos);

        var lastPositions = playerHighlights.getOrDefault(playerId, Set.of());
        if (!newPositions.equals(lastPositions)) {
            playerHighlights.put(playerId, new HashSet<>(newPositions));
            ServerPlayNetworking.send(player, new HighlightBlocksPayload(new ArrayList<>(newPositions)));
        }
    }

    private void removePlayerHighlight(ServerPlayer player) {
        playerHighlights.remove(player.getStringUUID());
    }
}

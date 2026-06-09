package org.inferis.manicminer.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class VeinMinerSession {
    private static ArrayList<VeinMinerSession> sessions = new ArrayList<>();

    public ServerPlayerEntity player;
    public ServerWorld world;
    public Set<BlockPos> positions;
    public BlockPos initialPos;
    private boolean isDryRun;

    public static VeinMinerSession sessionForPlayer(ServerPlayerEntity player) {
        for (var session: sessions) {
            if (session.player == player) {
                return session;
            }
        }
        return null;
    }

    public static VeinMinerSession sessionForPosition(BlockPos position) {
        for (var session: sessions) {
            if (session.positions.contains(position)) {
                return session;
            }
        }
        return null;
    }

    public static VeinMinerSession start(ServerPlayerEntity player, ServerWorld world, BlockPos initialPos) {
        var session = new VeinMinerSession(player, world, initialPos, false);
        sessions.add(session);
        return session;
    }

    public static VeinMinerSession preview(ServerPlayerEntity player, ServerWorld world, BlockPos initialPos) {
        return new VeinMinerSession(player, world, initialPos, true);
    }

    private static void finish(VeinMinerSession session) {
        sessions.remove(session);
    }

    private VeinMinerSession(ServerPlayerEntity player, ServerWorld world, BlockPos initialPos, boolean isDryRun) {
        this.player = player;
        this.world = world;
        this.initialPos = initialPos;
        this.isDryRun = isDryRun;
        this.positions = new HashSet<>();
        if (!isDryRun) {
            positions.add(initialPos);
        }
    }

    public boolean isDryRun() {
        return isDryRun;
    }

    public void addPosition(BlockPos pos) {
        positions.add(pos);
    }

    public void removePosition(BlockPos pos) {
        positions.remove(pos);
    }

    public void finish() {
        if (!isDryRun) {
            finish(this);
        }
    }
}

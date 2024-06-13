package org.inferis.manicminer.logic;

import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VeinMiner {
    private World world;
    private ServerPlayerEntity player;

    public VeinMiner(World world, ServerPlayerEntity player) {
        this.world = world;
        this.player = player;
    }

    public void mine(BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        CachedBlockPosition cachedPos = new CachedBlockPosition(world, pos, false);
        if (!player.getMainHandStack().canBreak(cachedPos)) {

        }
    }
}

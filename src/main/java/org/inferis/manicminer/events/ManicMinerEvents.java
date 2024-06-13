package org.inferis.manicminer.events;

import org.inferis.manicminer.logic.VeinMiner;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManicMinerEvents {
    public static void afterBlockBreak(World world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
        VeinMiner miner = new VeinMiner(world, player);
        miner.mine(pos, state);
    }    
}

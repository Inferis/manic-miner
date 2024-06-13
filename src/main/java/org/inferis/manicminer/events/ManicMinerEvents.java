package org.inferis.manicminer.events;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManicMinerEvents {
    public static void afterBlockBreak(World world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
    }    
}

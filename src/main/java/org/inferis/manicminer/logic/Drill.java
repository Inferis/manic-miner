package org.inferis.manicminer.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface Drill {
    boolean canHandle(BlockState blockState);
    boolean isRightTool(BlockPos pos);
    boolean drill(BlockPos blockPos);
}
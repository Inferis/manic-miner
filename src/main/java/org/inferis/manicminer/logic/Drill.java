package org.inferis.manicminer.logic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface Drill {
    boolean canHandle(BlockState blockState);
    boolean isRightTool(BlockPos pos);
    boolean drill(BlockPos blockPos);
}
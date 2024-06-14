package org.inferis.manicminer.logic;

import net.minecraft.util.math.BlockPos;

public interface Drill {
    boolean canHandle(String blockId);
    boolean isRightTool(BlockPos pos);
    boolean drill(BlockPos blockPos);
}
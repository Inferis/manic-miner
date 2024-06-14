package org.inferis.manicminer.logic.drills;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoodDrill extends DrillBase {

    public WoodDrill(World world, ServerPlayerEntity player) {
        super(world, player);
    }

    @Override
    public boolean canHandle(String blockId) {
        return blockId.contains(":log") || blockId.endsWith("stem");
    }

    @Override
    public boolean drill(BlockPos blockPos) {
        _log("Wood");
        return true;
    }
    
}

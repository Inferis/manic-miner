package org.inferis.manicminer.logic.drills;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IceDrill extends DrillBase {

    public IceDrill(World world, ServerPlayerEntity player) {
        super(world, player);
    }

    @Override
    public boolean canHandle(String blockId) {
        return blockId.endsWith("ice"); 
    }

    @Override
    public boolean drill(BlockPos blockPos) {
        _log("Ice");
        return true;
    }
    
}

package org.inferis.manicminer.logic.drills;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreDrill extends DrillBase {

    public OreDrill(World world, ServerPlayerEntity player) {
        super(world, player);
    }

    @Override
    public boolean canHandle(String blockId) {
        return 
            blockId.endsWith("_ore") ||
            blockId == "minecraft:ancient_debris" ||
            blockId == "minecraft:magma";
    }

    @Override
    public boolean drill(BlockPos blockPos) {
        _log("Ore");
        player.interactionManager.tryBreakBlock(blockPos);
        return true;
    }

}

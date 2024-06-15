package org.inferis.manicminer.logic.drills;

import org.inferis.manicminer.ManicMiner;

import java.util.ArrayDeque;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WoodDrill extends DrillBase {

    public WoodDrill(World world, ServerPlayerEntity player) {
        super(world, player);
    }

    @Override
    public boolean canHandle(String blockId) {
        return blockId.endsWith("log") || blockId.endsWith("stem") || blockId.endsWith("leaves");
    }

    @Override
    public boolean drill(BlockPos startPos) {
        var processed = 0;
        var pending = new ArrayDeque<BlockPos>();
        pending.add(startPos);

        while (!pending.isEmpty() && processed < ManicMiner.CONFIG.maxVeinSize) {
            BlockPos woodPos = pending.remove();
            var woodBlock = world.getBlockState(woodPos).getBlock();
            if (player.interactionManager.tryBreakBlock(woodPos)) {
                ++processed;
                
                // look around current block
                forXYZ(woodPos, 1, newPos -> {
                    var newBlock = world.getBlockState(newPos).getBlock();
                    if (blocksMatch(newBlock, woodBlock) && !pending.contains(newPos)) {
                        pending.add(newPos);
                    }
                });
            }
        }

        return true;
    }

    private boolean blocksMatch(Block blockA, Block blockB) {
        var blockARoot = Registries.BLOCK.getId(blockA).toString().replace("_log", "").replace("_leaves", "");
        var blockBRoot = Registries.BLOCK.getId(blockB).toString().replace("_log", "").replace("_leaves", "");

        return blockARoot.equals(blockBRoot);
    }
    
}

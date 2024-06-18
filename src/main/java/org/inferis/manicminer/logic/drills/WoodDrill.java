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
        var pendingLogs = new ArrayDeque<BlockPos>();
        var logBlocks = new ArrayDeque<BlockPos>();
        pendingLogs.add(startPos);

        // have to get this id here while the actual block is still there (iso air)
        var leavesBlockId = Registries.BLOCK.getId(world.getBlockState(startPos).getBlock()).toString().replace("_log", "_leaves");

        while (!pendingLogs.isEmpty() && processed < ManicMiner.CONFIG.maxWoodSize) {
            BlockPos woodPos = pendingLogs.remove();
            var woodBlock = world.getBlockState(woodPos).getBlock();
            
            if (player.interactionManager.tryBreakBlock(woodPos)) {
                logBlocks.add(woodPos);
                processed += 1;
                
                // look around current block
                forXYZ(woodPos, 1, newPos -> {
                    var newBlock = world.getBlockState(newPos).getBlock();
                    if (newBlock == woodBlock && !pendingLogs.contains(newPos)) {
                        pendingLogs.add(newPos);
                    }
                }, true);
            }
        }

        // second round, leaves
        // The pending blocks are all air now, 
        var pendingLeaves = logBlocks;
        _log("leaves id = " + leavesBlockId);
        while (!pendingLeaves.isEmpty() && processed < ManicMiner.CONFIG.maxWoodSize) {
            // remove the immediately surrounding leaves around the log blocks
            processed += forXYZ(pendingLeaves.remove(), 1, newPos -> {
                int processedLeaves = 0;
                var newBlock = world.getBlockState(newPos).getBlock();
                var newBlockId = Registries.BLOCK.getId(newBlock).toString();
                if (newBlockId.equals(leavesBlockId)) {
                    if (player.interactionManager.tryBreakBlock(newPos)) {
                        processedLeaves += 1;
                    }
                }
                return processedLeaves;
            }, true);
        }

        _log("processed = " + processed);
        return true;
    }

    private boolean isLeaf(Block block) {
        return Registries.BLOCK.getId(block).toString().endsWith("_leaves");
    }
    
}

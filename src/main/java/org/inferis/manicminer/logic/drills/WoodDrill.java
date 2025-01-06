package org.inferis.manicminer.logic.drills;

import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.inferis.manicminer.ManicMiner;
import org.inferis.manicminer.logic.VeinMinerSession;

import java.util.ArrayDeque;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

public class WoodDrill extends DrillBase {

    public WoodDrill(VeinMinerSession session) {
        super(session);
    }

    public static final TagKey<Block> woodTag = TagKey.of(RegistryKeys.BLOCK, Identifier.of("manicminer", "wood"));

    @Override
    public boolean canHandle(BlockState blockState) {
        return blockState.isIn(woodTag);
    }

    @Override
    public boolean drill(BlockPos startPos) {
        var world = session.world;
        var broken = 0;
        var pendingLogs = new ArrayDeque<BlockPos>();
        var logBlocks = new ArrayDeque<BlockPos>();
        pendingLogs.add(startPos);

        // have to get this id here while the actual block is still there (iso air)
        var leavesBlockId = Registries.BLOCK.getId(world.getBlockState(startPos).getBlock()).toString().replace("_log", "_leaves");

        while (!pendingLogs.isEmpty() && broken < ManicMiner.CONFIG.maxWoodSize) {
            BlockPos woodPos = pendingLogs.remove();
            var woodBlock = world.getBlockState(woodPos).getBlock();
            
            if (tryBreakBlock(woodPos)) {
                logBlocks.add(woodPos);
                broken += 1;
                
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
        while (!pendingLeaves.isEmpty() && broken < ManicMiner.CONFIG.maxWoodSize) {
            // remove the immediately surrounding leaves around the log blocks
            broken += forXYZ(pendingLeaves.remove(), 1, newPos -> {
                int brokenLeaves = 0;
                var newBlock = world.getBlockState(newPos).getBlock();
                var newBlockId = Registries.BLOCK.getId(newBlock).toString();
                if (newBlockId.equals(leavesBlockId)) {
                    if (tryBreakBlock(newPos)) {
                        brokenLeaves += 1;
                    }
                }
                return brokenLeaves;
            }, true);
        }

        return true;
    }

    private boolean isLeaf(Block block) {
        return Registries.BLOCK.getId(block).toString().endsWith("_leaves");
    }
    
}

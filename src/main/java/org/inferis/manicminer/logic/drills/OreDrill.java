package org.inferis.manicminer.logic.drills;

import org.inferis.manicminer.ManicMiner;

import java.util.ArrayDeque;

import net.minecraft.registry.Registries;
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
    public boolean drill(BlockPos startPos) {
        var initialBlock = world.getBlockState(startPos).getBlock();
        var processed = 0;
        var pending = new ArrayDeque<BlockPos>();
        pending.add(startPos);

        while (!pending.isEmpty() && processed < ManicMiner.CONFIG.maxVeinSize) {
            BlockPos orePos = pending.remove();
            var oreBlock = world.getBlockState(orePos).getBlock();
            if (player.interactionManager.tryBreakBlock(orePos)) {
                if (oreBlock == initialBlock) {
                    ++processed;
                }
                
                if (oreBlock == initialBlock) {
                    // look around current block
                    forXYZ(orePos, 1, newPos -> {
                        var newBlock = world.getBlockState(newPos).getBlock();
                        var newBlockId = Registries.BLOCK.getId(newBlock).toString();
                        if (!pending.contains(newPos) && (!canHandle(newBlockId) || newBlock == oreBlock)) {
                            pending.add(newPos);
                        }
                    });
                }
            }
        }

        return true;
    }

}

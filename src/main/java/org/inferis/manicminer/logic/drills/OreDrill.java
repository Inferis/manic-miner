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
        var brokenOre = 0;
        var brokenCommon = 0;
        var pending = new ArrayDeque<BlockPos>();
        pending.add(startPos);

        while (!pending.isEmpty() && brokenOre < ManicMiner.CONFIG.maxVeinSize && brokenCommon < ManicMiner.CONFIG.maxCommonSize) {
            BlockPos orePos = pending.remove();
            var oreBlock = world.getBlockState(orePos).getBlock();
            if (tryBreakBlock(orePos)) {
                if (oreBlock == initialBlock) {
                    brokenOre += 1;
                }
                else {
                    brokenCommon += 1;
                }
                
                if (oreBlock == initialBlock) {
                    // look around current block
                    forXYZ(orePos, 1, newPos -> {
                        var newBlock = world.getBlockState(newPos).getBlock();
                        var newBlockId = Registries.BLOCK.getId(newBlock).toString();
                        var isSameOreBlock = newBlock == oreBlock;
                        var isNonOreBlock = !canHandle(newBlockId) && ManicMiner.CONFIG.removeCommonBlocksAroundOre;
                        if (!pending.contains(newPos) && (isSameOreBlock || isNonOreBlock)) {
                            pending.add(newPos);
                        }
                    });
                }
            }
        }

        return true;
    }

}

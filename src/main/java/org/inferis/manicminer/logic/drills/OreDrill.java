package org.inferis.manicminer.logic.drills;

import org.inferis.manicminer.ManicMiner;
import org.inferis.manicminer.logic.VeinMinerSession;

import java.util.ArrayDeque;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class OreDrill extends DrillBase {

    public OreDrill(VeinMinerSession session) {
        super(session);
    }

    public static final TagKey<Block> oreTag = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("manicminer", "ore"));

    @Override
    public boolean canHandle(BlockState blockState) {
        return blockState.is(oreTag);
    }

    @Override
    public boolean drill(BlockPos startPos) {
        var world = session.world;
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
                        var newBlockState = world.getBlockState(newPos);
                        var newBlock = newBlockState.getBlock();
                        var isSameOreBlock = newBlock == oreBlock;
                        var isNonOreBlock = !canHandle(newBlockState) && ManicMiner.CONFIG.removeCommonBlocksAroundOre;
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

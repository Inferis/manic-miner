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

public class CommonDrill extends DrillBase {
    public CommonDrill(VeinMinerSession session) {
        super(session);
    }

    public static final TagKey<Block> commonTag = TagKey.of(RegistryKeys.BLOCK, Identifier.of("manicminer", "common"));

    @Override
    public boolean canHandle(BlockState blockState) {
        return ManicMiner.CONFIG.allowCommonBlocks && blockState.isIn(commonTag);
    }

    @Override
    public boolean drill(BlockPos startPos) {
        var world = session.world;
        var player = session.player;
        var initialBlock = world.getBlockState(startPos).getBlock();
        var initialBlockId = Registries.BLOCK.getId(initialBlock).toString();
        var forceVertical = initialBlockId.equals("minecraft:dripstone_block"); // we want to mine up/down for dripstone
        var broken = 0;
        var pending = new ArrayDeque<BlockPos>();
        pending.add(startPos);

        while (!pending.isEmpty() && broken < ManicMiner.CONFIG.maxCommonSize) {
            BlockPos blockPos = pending.remove();
            var blockState = world.getBlockState(blockPos);
            if (tryBreakBlock(blockPos)) {
                ++broken;
                // if (!world.isClient) {
                //     var builder = new LootContextParameterSet.Builder((ServerWorld)world)
                //         .add(LootContextParameters.TOOL, player.getMainHandStack())
                //         .add(LootContextParameters.ORIGIN, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                //     var itemStacks = blockState.getDroppedStacks(builder);

                // }
                
                // look around current block
                forXYZ(blockPos, 2, newPos -> {
                    var block = world.getBlockState(newPos).getBlock();
                    if (blocksMatch(block, initialBlock) && !pending.contains(newPos)) {
                        var lookingDown = player.getPitch() > 33.0;
                        if (lookingDown || newPos.getY() >= player.getBlockY()) {
                            pending.add(newPos);
                        } 
                    }
                }, forceVertical);
            }
        }

        return true;
    }

    private boolean blocksMatch(Block blockA, Block blockB) {
        if (blockA == blockB) {
            return true;
        }

        // special case for dirt
        var blockAId = Registries.BLOCK.getId(blockA).toString();
        var blockBId = Registries.BLOCK.getId(blockB).toString();

        var isBlockADirt = blockAId.equals("minecraft:dirt") || blockAId.equals("minecraft:grass_block") || blockAId.equals("minecraft:dirt_path");
        var isBlockBDirt = blockBId.equals("minecraft:dirt") || blockBId.equals("minecraft:grass_block") || blockBId.equals("minecraft:dirt_path");

        return isBlockADirt && isBlockBDirt;
    }
    
}

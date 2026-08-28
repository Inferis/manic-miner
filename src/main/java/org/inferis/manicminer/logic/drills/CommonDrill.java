package org.inferis.manicminer.logic.drills;

import org.inferis.manicminer.ManicMiner;
import org.inferis.manicminer.logic.VeinMinerSession;

import java.util.ArrayDeque;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CommonDrill extends DrillBase {
    public CommonDrill(VeinMinerSession session) {
        super(session);
    }

    public static final TagKey<Block> commonTag = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("manicminer", "common"));

    @Override
    public boolean canHandle(BlockState blockState) {
        return ManicMiner.CONFIG.allowCommonBlocks && blockState.is(commonTag);
    }

    @Override
    public boolean drill(BlockPos startPos) {
        var world = session.world;
        var player = session.player;
        var initialBlock = world.getBlockState(startPos).getBlock();
        var initialBlockId = BuiltInRegistries.BLOCK.getKey(initialBlock).toString();
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
                        var lookingDown = player.getXRot() > 33.0;
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
        var blockAId = BuiltInRegistries.BLOCK.getKey(blockA).toString();
        var blockBId = BuiltInRegistries.BLOCK.getKey(blockB).toString();

        var isBlockADirt = blockAId.equals("minecraft:dirt") || blockAId.equals("minecraft:grass_block") || blockAId.equals("minecraft:dirt_path");
        var isBlockBDirt = blockBId.equals("minecraft:dirt") || blockBId.equals("minecraft:grass_block") || blockBId.equals("minecraft:dirt_path");

        return isBlockADirt && isBlockBDirt;
    }
    
}

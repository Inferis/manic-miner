package org.inferis.manicminer.logic.drills;

import org.inferis.manicminer.ManicMiner;

import java.util.ArrayDeque;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonDrill extends DrillBase {

    public CommonDrill(World world, ServerPlayerEntity player) {
        super(world, player);
    }

    @Override
    public boolean canHandle(String blockId) {
        return ManicMiner.CONFIG.allowCommonBlocks && (
            blockId.equals("minecraft:stone") || blockId.equals("minecraft:deepslate") ||
            blockId.equals("minecraft:granite") || blockId.equals("minecraft:andesite") || blockId.equals("minecraft:diorite") ||
            blockId.equals("minecraft:gravel") || blockId.equals("minecraft:tuff") ||
            blockId.equals("minecraft:netherrack") || blockId.equals("minecraft:basalt") || blockId.equals("minecraft:blackstone") || 
            blockId.equals("minecraft:hardened_clay") || blockId.equals("minecraft:stained_hardened_clay") ||
            blockId.equals("minecraft:sandstone") || blockId.equals("minecraft:sand") || 
            blockId.equals("minecraft:soulsand") || blockId.equals("minecraft:soulsoil") || 
            blockId.equals("minecraft:dirt") || blockId.equals("minecraft:grass_block") || 
            blockId.equals("minecraft:clay") ||
            blockId.equals("minecraft:dripstone_block") ||
            blockId.endsWith("ice")
        );
    }

    @Override
    public boolean drill(BlockPos startPos) {
        var initialBlock = world.getBlockState(startPos).getBlock();
        var initialBlockId = Registries.BLOCK.getId(initialBlock).toString();
        var forceVertical = initialBlockId.equals("minecraft:dripstone_block"); // we want to mine up/down for dripstone
        var broken = 0;
        var pending = new ArrayDeque<BlockPos>();
        pending.add(startPos);

        while (!pending.isEmpty() && broken < ManicMiner.CONFIG.maxCommonSize) {
            BlockPos blockPos = pending.remove();
            if (tryBreakBlock(blockPos)) {
                ++broken;
                
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

        return
            (blockAId.equals("minecraft:dirt") && blockBId.equals("minecraft:grass_block")) ||
            (blockAId.equals("minecraft:grass_block") && blockBId.equals("minecraft:dirt"));
    }
    
}

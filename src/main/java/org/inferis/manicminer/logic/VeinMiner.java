package org.inferis.manicminer.logic;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VeinMiner {
    private World world;
    private ServerPlayerEntity player;

    public VeinMiner(World world, ServerPlayerEntity player) {
        this.world = world;
        this.player = player;
    }

    public void mine(BlockPos startPos) {
        Queue<BlockPos> positions = new ArrayDeque<BlockPos>();
        positions.add(startPos);

        _log("starting " + startPos.toShortString());
        while (!positions.isEmpty()) {
            BlockPos pos = positions.poll();
            _log("handling " + pos.toShortString());

            // see if we can break the block
            CachedBlockPosition cachedPos = new CachedBlockPosition(world, pos, false);
            if (!player.getMainHandStack().canBreak(cachedPos)) {
                _log("can't break");
                continue;
            }

            // BlockState state = world.getBlockState(pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            _log("broke");
        }
    }

    private void _log(String message) {
        PlainTextContent literal = new PlainTextContent.Literal(message);
        Text text = MutableText.of(literal);
        player.sendMessage(text);
    }
}

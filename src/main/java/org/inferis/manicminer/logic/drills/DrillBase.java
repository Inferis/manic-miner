package org.inferis.manicminer.logic.drills;

import org.inferis.manicminer.logic.Drill;
import org.inferis.manicminer.ManicMiner;

import java.util.ArrayList;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DrillBase implements Drill {
    protected World world;
    protected ServerPlayerEntity player;

    public DrillBase(World world, ServerPlayerEntity player) {
        this.world = world;
        this.player = player;
    }

    @Override
    public boolean canHandle(String blockId) {

        return false;
    }

    @Override
    public boolean drill(BlockPos blockPos) {
        return false;
    }

    @Override
    public boolean isRightTool(BlockPos pos) {
        var blockState = world.getBlockState(pos);
        return player.getMainHandStack().isSuitableFor(blockState);
    }

    protected interface ForXYZHandler {
        public void handle(BlockPos pos);
    }

    protected void forXYZ(BlockPos pos, int max, ForXYZHandler handler) {
        var offsets = new ArrayList<Integer>();
        for (var d = 0; d <= max; ++d) {
            offsets.add(d);
            if (d != -d) {
                offsets.add(-d);
            }
        }

        var order = new String[] { "x", "y", "z" };
        for (var i1: offsets) {
            for (var i2: offsets) {
                for (var i3: offsets) {
                    _log("offsets = " + i1 + " " + i2 + " " + i3);
                    var ix = order[0] == "x" ? i1 : order[1] == "x" ? i2 : i3;
                    var iy = order[0] == "y" ? i1 : order[1] == "y" ? i2 : i3;
                    var iz = order[0] == "z" ? i1 : order[1] == "z" ? i2 : i3;
    
                    var px = pos.getX() + ix;
                    var py = pos.getY() + iy;
                    var pz = pos.getZ() + iz;
        
                    handler.handle(new BlockPos(px, py, pz));
                }
            }
        }
    }

    protected void _log(String message) {
        PlainTextContent literal = new PlainTextContent.Literal(message);
        Text text = MutableText.of(literal);
        player.sendMessage(text);
        ManicMiner.LOGGER.info(message);
    }
}
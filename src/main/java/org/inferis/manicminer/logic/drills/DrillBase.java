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

    protected interface ForXYZCounter {
        public int handle(BlockPos pos);
    }

    protected void forXYZ(BlockPos pos, int max, ForXYZHandler handler) {
        forXYZ(pos, max, handlerPos -> { 
            handler.handle(handlerPos); 
            return 0; 
        }, false);
    }

    protected int forXYZ(BlockPos pos, int max, ForXYZCounter handler) {
        return forXYZ(pos, max, handler, false);
    }

    protected void forXYZ(BlockPos pos, int max, ForXYZHandler handler, boolean forceVertical) {
        forXYZ(pos, max, handlerPos -> { 
            handler.handle(handlerPos); 
            return 0; 
        }, forceVertical);
    }

    protected int forXYZ(BlockPos pos, int max, ForXYZCounter handler, boolean forceVertical) {
        // Create the offsets for one axis.
        var offsets = new ArrayList<Integer>();
        for (var d = 0; d <= max; ++d) {
            offsets.add(d);
            if (d != -d) {
                offsets.add(-d);
            }
        }

        // Change the order in which we will be processing blocks
        // depending on where we are looking, so we can create a "tunnel" effect.
        // This will be mostly handy for the "common" path where we don't 
        // have actual veins to mine.
        var order = new String[] { "x", "y", "z" };
        if (forceVertical) {
            order = new String[] { "y", "x", "z" };
        }
        else {
            var majorPitchChange = player.getPitch() < -45.0 || player.getPitch() > 45.0; 
            var majorYawChange = (player.getYaw() > 45.0 && player.getYaw() < 135.0) || (player.getYaw() < -45.0 && player.getYaw() > -135.0); 
            if (majorPitchChange) {
                if (majorYawChange) {
                    order = new String[] { "y", "z", "x" };
                }
                else {
                    order = new String[] { "y", "x", "z" };
                }
            }
            else {
                if (majorYawChange) {
                    order = new String[] { "z", "y", "x" };
                }
            }
        }

        // Generate positions based off the offsets generated,
        // using the order we determined above.
        int counter = 0;
        for (var i1: offsets) {
            for (var i2: offsets) {
                for (var i3: offsets) {
                    var ix = order[0] == "x" ? i1 : order[1] == "x" ? i2 : i3;
                    var iy = order[0] == "y" ? i1 : order[1] == "y" ? i2 : i3;
                    var iz = order[0] == "z" ? i1 : order[1] == "z" ? i2 : i3;
    
                    var px = pos.getX() + ix;
                    var py = pos.getY() + iy;
                    var pz = pos.getZ() + iz;
        
                    counter += handler.handle(new BlockPos(px, py, pz));
                }
            }
        }

        return counter;
    }

    protected boolean tryBreakBlock(BlockPos blockPos) {
        // we need a tool in order to break a block, so check always
        return isRightTool(blockPos) && player.interactionManager.tryBreakBlock(blockPos);
    }

    protected void _log(String message) {
        PlainTextContent literal = new PlainTextContent.Literal(message);
        Text text = MutableText.of(literal);
        player.sendMessage(text);
        ManicMiner.LOGGER.info(message);
    }
}
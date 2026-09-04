package org.inferis.manicminer.logic.drills;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import org.inferis.manicminer.logic.Drill;
import org.inferis.manicminer.logic.VeinMinerSession;
import org.inferis.manicminer.ManicMiner;

import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.level.block.state.BlockState;

public class DrillBase implements Drill {
    protected VeinMinerSession session;

    public DrillBase(VeinMinerSession session) {
        this.session = session;
    }

    @Override
    public boolean canHandle(BlockState blockId) {
        return false;
    }

    @Override
    public boolean drill(BlockPos blockPos) {
        return false;
    }

    @Override
    public boolean isRightTool(BlockPos pos) {
        var blockState = session.world.getBlockState(pos);
        return session.player.getMainHandItem().isCorrectToolForDrops(blockState);
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
            var player = session.player;
            var majorPitchChange = player.getXRot() < -45.0 || player.getXRot() > 45.0; 
            var majorYawChange = (player.getYRot() > 45.0 && player.getYRot() < 135.0) || (player.getYRot() < -45.0 && player.getYRot() > -135.0); 
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
        if (session.isDryRun()) {
            if (session.positions.contains(blockPos)) {
                return false; // already previewed, avoid re-processing
            }
            var canBreak = isRightTool(blockPos);
            if (canBreak) {
                session.addPosition(blockPos);
            }
            return canBreak;
        }
        // we need a tool in order to break a block, so check always
        session.addPosition(blockPos);
        var blockState = session.world.getBlockState(blockPos); // need to get this before destroying
        var success = isRightTool(blockPos) && session.player.gameMode.destroyBlock(blockPos);
        if (success) {
            session.world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
        }
        else {
            session.removePosition(blockPos);
        }
        return success;
    }

    protected void _log(String message) {
        PlainTextContents literal = new PlainTextContents.LiteralContents(message);
        Component text = MutableComponent.create(literal);
        session.player.sendSystemMessage(text);
        ManicMiner.LOGGER.info(message);
    }
}
package org.inferis.manicminer.events;

import org.inferis.manicminer.logic.Drill;
import org.inferis.manicminer.logic.drills.*;
import java.util.ArrayList;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManicMinerEvents {
    private static ArrayList<ServerPlayerEntity> veinminingPlayers = new ArrayList<ServerPlayerEntity>();

    public static boolean beforeBlockBreak(World world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
        // tryBlockBreak will recurse into this, so we want to avoid calling this for the subsequent mined blocks
        if (player.isInSneakingPose() && !veinminingPlayers.contains(player)) {
            veinminingPlayers.add(player);

            var shouldContinue = !mine(world, player, pos);

            veinminingPlayers.remove(player);
            return shouldContinue;
        }
        else {
            return true;
        }
    }    

    public static void afterBlockBreak(World world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
    }    

    private static boolean mine(World world, ServerPlayerEntity player, BlockPos pos) {
        var drills = new Drill[] {
            new OreDrill(world, player),
            new WoodDrill(world, player),
            new IceDrill(world, player),
            new CommonDrill(world, player)
        };

        var blockState = world.getBlockState(pos);
        var blockId = Registries.BLOCK.getId(blockState.getBlock()).toString();
        for (var drill: drills) {
            if (drill.canHandle(blockId) && drill.isRightTool(pos)) {
                return drill.drill(pos);
            }
        }

        return false;
    }
}

package org.inferis.manicminer.events;

import org.inferis.manicminer.ManicMiner;
import org.inferis.manicminer.logic.Drill;
import org.inferis.manicminer.logic.VeinMinerSession;
import org.inferis.manicminer.logic.drills.*;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class ManicMinerEvents {
    public static boolean beforeBlockBreak(World world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return true;
        }

        // tryBlockBreak will recurse into this, so we want to avoid calling this for the subsequent mined blocks.
        var isVeinMining = VeinMinerSession.sessionForPlayer(player) != null;
        if (canVeinMine(player) && !isVeinMining) {
            var session = VeinMinerSession.start(player, (ServerWorld)world, pos);
            var shouldContinue = !mine(session);
            session.finish();
            return shouldContinue;
        }
        else {
            return true;
        }
    }

    public static boolean canVeinMine(ServerPlayerEntity player) {
        if (ManicMiner.CONFIG.mustSneak && !player.isInSneakingPose()) {
            return false;
        }
        if (ManicMiner.CONFIG.requiresHotKey && !ManicMiner.IsPlayerPressingHotKey(player)) {
            return false;
        }
        if (ManicMiner.CONFIG.requireEnchantment) {
            for (var entry : player.getMainHandStack().getEnchantments().getEnchantments()) {
                if (entry.getIdAsString().equalsIgnoreCase("manicminer:manic_mining")) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static Set<BlockPos> previewBlockMine(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        var blockState = world.getBlockState(pos);
        var session = VeinMinerSession.preview(player, world, pos);
        var drills = new Drill[] {
            new OreDrill(session),
            new WoodDrill(session),
            new CommonDrill(session)
        };
        for (var drill : drills) {
            if (drill.canHandle(blockState) && drill.isRightTool(pos)) {
                drill.drill(pos);
                return session.positions;
            }
        }
        return Set.of();
    }

    public static void onEntityLoad(Entity entity, ServerWorld world) {
        if (ManicMiner.CONFIG.summonItems) {
            var pos = entity.getBlockPos();
            var session = VeinMinerSession.sessionForPosition(pos);
            if (session != null) {
                entity.setPos(session.initialPos.getX(), session.initialPos.getY(), session.initialPos.getZ());
            }
        }
    }

    private static boolean mine(VeinMinerSession session) {
        // Check ores first, than wood, than ice and than the common blocks (stone etc).
        var drills = new Drill[] {
            new OreDrill(session),
            new WoodDrill(session),
            new CommonDrill(session)
        };

        var pos = session.initialPos;
        var blockState = session.world.getBlockState(pos);
        for (var drill: drills) {
            if (drill.canHandle(blockState) && drill.isRightTool(pos)) {
                return drill.drill(pos);
            }
        }

        return false;
    }
}

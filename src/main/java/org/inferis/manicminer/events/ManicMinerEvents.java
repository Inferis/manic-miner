package org.inferis.manicminer.events;

import org.inferis.manicminer.ManicMiner;
import org.inferis.manicminer.logic.Drill;
import org.inferis.manicminer.logic.VeinMinerSession;
import org.inferis.manicminer.logic.drills.*;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ManicMinerEvents {
    public static boolean beforeBlockBreak(World world, ServerPlayerEntity player, BlockPos pos, BlockState state) {
        if (world.isClient) {
            return true;
        }

        // tryBlockBreak will recurse into this, so we want to avoid calling this for the subsequent mined blocks.
        var isVeinMining = VeinMinerSession.sessionForPlayer(player) != null;
        var canVeinMine = true;
        if (ManicMiner.CONFIG.mustSneak) {
            canVeinMine = canVeinMine && player.isInSneakingPose();
        }
        if (ManicMiner.CONFIG.requiresHotKey) {
            canVeinMine = canVeinMine && ManicMiner.IS_HOTKEY_PRESSED;
        }
        if (ManicMiner.CONFIG.requireEnchantment) {
            var hasEnchantment = false;
            for (var enchantmentEntry: player.getMainHandStack().getEnchantments().getEnchantments()) {
                if (enchantmentEntry.getIdAsString().equalsIgnoreCase("manicminer:manic_mining")) {
                    hasEnchantment = true;
                    break;
                }
            }
            canVeinMine = canVeinMine && hasEnchantment;
        }
        if (canVeinMine && !isVeinMining) {
            var session = VeinMinerSession.start(player, (ServerWorld)world, pos);
            var shouldContinue = !mine(session);
            session.finish();
            return shouldContinue;
        }
        else {
            return true;
        }
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
        var blockId = Registries.BLOCK.getId(blockState.getBlock()).toString();
        for (var drill: drills) {
            if (drill.canHandle(blockId) && drill.isRightTool(pos)) {
                return drill.drill(pos);
            }
        }

        return false;
    }
}

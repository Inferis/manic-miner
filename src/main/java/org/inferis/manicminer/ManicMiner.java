package org.inferis.manicminer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldEvents;

import org.inferis.manicminer.events.ManicMinerEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManicMiner implements ModInitializer {
	public static final String MODID = "manic-miner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ManicMinerConfig CONFIG = new ManicMinerConfig();

	@Override
	public void onInitialize() {
		ManicMiner.CONFIG.initialLoad();

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (player instanceof ServerPlayerEntity serverPlayer) {
				return ManicMinerEvents.beforeBlockBreak(world, serverPlayer, pos, state);
			}
			else {
				return true;
			}
		});

		ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerWorld world) -> {
			ManicMinerEvents.onEntityLoad(entity, world);
		});
	}
}
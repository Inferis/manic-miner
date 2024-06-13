package org.inferis.manicminer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import org.inferis.manicminer.events.ManicMinerEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManicMiner implements ModInitializer {
	public static final String MODID = "manic-miner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (player instanceof ServerPlayerEntity serverPlayer) {
				ManicMinerEvents.afterBlockBreak(world, serverPlayer, pos, state);
			}
		});
	}
}
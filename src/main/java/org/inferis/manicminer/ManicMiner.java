package org.inferis.manicminer;

import org.inferis.manicminer.events.ManicMinerEvents;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManicMiner implements ModInitializer {
	public static final String MODID = "manic-miner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ManicMinerConfig CONFIG = new ManicMinerConfig();

	public static HotkeyPressedTracker HOTKEY_PRESSED_TRACKER = new HotkeyPressedTracker();
	private final PositionHighlightTracker highlightTracker = new PositionHighlightTracker();

	@Override
	public void onInitialize() {
		ManicMiner.CONFIG.initialLoad();

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (player instanceof ServerPlayer serverPlayer) {
				return ManicMinerEvents.beforeBlockBreak(world, serverPlayer, pos, state);
			}
			else {
				return true;
			}
		});

		ServerEntityEvents.ENTITY_LOAD.register(ManicMinerEvents::onEntityLoad);
	}
}

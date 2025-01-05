package org.inferis.manicminer;

import org.inferis.manicminer.events.ManicMinerEvents;
import org.inferis.manicminer.networking.HotKeyPressedPayload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManicMiner implements ModInitializer {
	public static final String MODID = "manic-miner";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final ManicMinerConfig CONFIG = new ManicMinerConfig();
	public static Boolean IS_HOTKEY_PRESSED = false;
	
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

		registerPayloadAndReceiver();
	}

	public void registerPayloadAndReceiver() {
		// Register Payload
		PayloadTypeRegistry.playC2S().register(HotKeyPressedPayload.ID, HotKeyPressedPayload.CODEC);

		// Register payload receiver. The client will send the payload to the server, which
		// will then in turn set the right mode on the magnet items.
		ServerPlayNetworking.registerGlobalReceiver(HotKeyPressedPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				IS_HOTKEY_PRESSED = payload.isPressed();
			});
		});
	}
}
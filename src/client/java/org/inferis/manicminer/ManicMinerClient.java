package org.inferis.manicminer;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.inferis.manicminer.networking.HighlightBlocksPayload;
import org.inferis.manicminer.networking.HotKeyPressedPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.mojang.blaze3d.platform.InputConstants;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import org.inferis.manicminer.renderer.PositionHighlightRenderer;

public class ManicMinerClient implements ClientModInitializer {
	private static final KeyMapping.Category MANIC_MINER_KEYS_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ManicMiner.MODID, "keys"));
	private KeyMapping hotKeyBinding;
	private Boolean isPressed = false;
	private final PositionHighlightRenderer highlightRenderer = new PositionHighlightRenderer();

	@Override
	public void onInitializeClient() {
		initializeHotkey();
		initializeHighlightPositions();
	}

	private void initializeHotkey() {
		hotKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"manicminer.key.hotkey_pressed",
				InputConstants.Type.KEYSYM,
				GLFW_KEY_V,
				MANIC_MINER_KEYS_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			var pressed = hotKeyBinding.isDown();
			if (isPressed != pressed) {
				isPressed = pressed;
				try {
					ClientPlayNetworking.send(new HotKeyPressedPayload(isPressed));
				}
				catch (IllegalStateException e) {
					ManicMiner.LOGGER.error("Can't send keypress packet", e);
				}
			}
		});
	}

	private void initializeHighlightPositions() {
		ClientPlayNetworking.registerGlobalReceiver(HighlightBlocksPayload.ID, (payload, context) -> {
			highlightRenderer.setPositions(payload.positions());
		});
	}
}

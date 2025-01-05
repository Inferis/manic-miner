package org.inferis.manicminer;

import org.inferis.manicminer.networking.HotKeyPressedPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ManicMinerClient implements ClientModInitializer {
	private KeyBinding hotKeyBinding;
	private Boolean isPressed = false;

	@Override
	public void onInitializeClient() {
		hotKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"manicminer.key.hotkey_pressed",
			InputUtil.Type.KEYSYM,
			InputUtil.GLFW_KEY_V,
			"manicminer.category.keys"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			var pressed = hotKeyBinding.isPressed();
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
}
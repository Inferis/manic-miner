package org.inferis.manicminer;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.resources.Identifier;
import org.inferis.manicminer.networking.HighlightBlocksPayload;
import org.inferis.manicminer.networking.HotKeyPressedPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;

public class ManicMinerClient implements ClientModInitializer {
	private static final KeyMapping.Category MANIC_MINER_KEYS_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ManicMiner.MODID, "keys"));
	private KeyMapping hotKeyBinding;
	private Boolean isPressed = false;

	private static List<BlockPos> highlightedPositions = List.of();

	@Override
	public void onInitializeClient() {
		hotKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
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

		ClientPlayNetworking.registerGlobalReceiver(HighlightBlocksPayload.ID, (payload, context) -> {
			highlightedPositions = payload.positions();
		});

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			var positions = highlightedPositions;
			if (positions.isEmpty()) return;

			var consumers = context.consumers();
			var matrices = context.matrices();

			var buffer = consumers.getBuffer(RenderTypes.LINES);
			var camera = context.gameRenderer().getMainCamera();

			matrices.pushPose();
			matrices.translate(-camera.position().x, -camera.position().y, -camera.position().z);

			var shape = Shapes.create(0.001, 0.001, 0.001, 0.999, 0.999, 0.999);
			for (var pos : positions) {
				ShapeRenderer.renderShape(matrices, buffer, shape, pos.getX(), pos.getY(), pos.getZ(), 0xCCFFD900, 2.0f);
			}

			matrices.popPose();
		});
	}
}

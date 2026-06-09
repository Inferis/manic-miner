package org.inferis.manicminer;

import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;
import org.inferis.manicminer.networking.HighlightBlocksPayload;
import org.inferis.manicminer.networking.HotKeyPressedPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ManicMinerClient implements ClientModInitializer {
	private static final KeyBinding.Category MANIC_MINER_KEYS_CATEGORY = KeyBinding.Category.create(Identifier.of(ManicMiner.MODID, "keys"));
	private KeyBinding hotKeyBinding;
	private Boolean isPressed = false;

	private static List<BlockPos> highlightedPositions = List.of();

	@Override
	public void onInitializeClient() {
		hotKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"manicminer.key.hotkey_pressed",
				InputUtil.Type.KEYSYM,
				InputUtil.GLFW_KEY_V,
				MANIC_MINER_KEYS_CATEGORY
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

		ClientPlayNetworking.registerGlobalReceiver(HighlightBlocksPayload.ID, (payload, context) -> {
			highlightedPositions = payload.positions();
		});

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			var positions = highlightedPositions;
			if (positions.isEmpty()) return;

			var consumers = context.consumers();
			if (consumers == null) return;

			var matrices = context.matrices();
			if (matrices == null) return;

			var buffer = consumers.getBuffer(RenderLayers.LINES);
			var camera = context.gameRenderer().getCamera();

			matrices.push();
			matrices.translate(-camera.getCameraPos().x, -camera.getCameraPos().y, -camera.getCameraPos().z);

			var shape = VoxelShapes.cuboid(0.001, 0.001, 0.001, 0.999, 0.999, 0.999);
			for (var pos : positions) {
				VertexRendering.drawOutline(matrices, buffer, shape, pos.getX(), pos.getY(), pos.getZ(), 0xCCFFD900, 2.0f);
			}

			matrices.pop();
		});
	}
}

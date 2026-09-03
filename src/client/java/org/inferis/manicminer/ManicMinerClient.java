package org.inferis.manicminer;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.resources.Identifier;
import org.inferis.manicminer.networking.HighlightBlocksPayload;
import org.inferis.manicminer.networking.HotKeyPressedPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;
import org.inferis.manicminer.renderer.ManicMinerRenderTypes;

import java.util.List;

import static net.fabricmc.fabric.api.event.Event.DEFAULT_PHASE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;

public class ManicMinerClient implements ClientModInitializer {
	private static final KeyMapping.Category MANIC_MINER_KEYS_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ManicMiner.MODID, "keys"));
	private KeyMapping hotKeyBinding;
	private Boolean isPressed = false;

	private static List<BlockPos> highlightedPositions = List.of();

	@Override
	public void onInitializeClient() {
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

		ClientPlayNetworking.registerGlobalReceiver(HighlightBlocksPayload.ID, (payload, context) -> {
			highlightedPositions = payload.positions();
		});

		LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register(DEFAULT_PHASE,(context, outlineRenderState) -> {
			return highlightedPositions.isEmpty();
		});

		LevelRenderEvents.END_MAIN.register(DEFAULT_PHASE,context -> {
			List<BlockPos> positions = highlightedPositions;
			if (positions.isEmpty()) return;

			var poseStack = context.poseStack();
			var camera = context.gameRenderer().mainCamera();

			poseStack.pushPose();
			poseStack.translate(-camera.position().x, -camera.position().y, -camera.position().z);

			var collector = context.submitNodeCollector();
			var shape = Shapes.create(0.001, 0.001, 0.001, 0.999, 0.999, 0.999);
			for (var pos: positions) {
				poseStack.pushPose();
				poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
				// Ghost outline visible through occluding blocks (ALWAYS_PASS depth, translucent)
				collector.submitShapeOutline(poseStack, shape, ManicMinerRenderTypes.linesAlwaysPassDepth(), 0x33FFD900, 1.5f, true);
				// Solid outline for visible parts (normal depth testing)
				collector.submitShapeOutline(poseStack, shape, RenderTypes.lines(), 0xFFFFD900, 3.0f, true);
				poseStack.popPose();
			}

			poseStack.popPose();
		});
	}
}

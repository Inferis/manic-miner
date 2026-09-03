package org.inferis.manicminer.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.List;

import static net.fabricmc.fabric.api.event.Event.DEFAULT_PHASE;

public class PositionHighlightRenderer {
    private List<BlockPos> highlightedPositions = List.of();

    public PositionHighlightRenderer() {
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

    public void setPositions(List<BlockPos> positions) {
        highlightedPositions = positions;
    }
}

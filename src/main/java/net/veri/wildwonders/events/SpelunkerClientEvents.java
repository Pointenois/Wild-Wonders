package net.veri.wildwonders.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.veri.wildwonders.effect.ModEffects;

@Mod.EventBusSubscriber(modid = "wildwonders", value = Dist.CLIENT)
public class SpelunkerClientEvents {
    private static final ResourceLocation VANILLA_ORES_TAG = new ResourceLocation("minecraft", "ores");
    private static final ResourceLocation FORGE_ORES_TAG = new ResourceLocation("forge", "ores");
    private static final int RADIUS = 12;

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.hasEffect(ModEffects.SPELUNKER.get())) return;

        PoseStack poseStack = event.getPoseStack();
        double camX = mc.gameRenderer.getMainCamera().getPosition().x();
        double camY = mc.gameRenderer.getMainCamera().getPosition().y();
        double camZ = mc.gameRenderer.getMainCamera().getPosition().z();

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());

        BlockPos playerPos = player.blockPosition();

        // Draw a box at the player's feet as a control
        drawBox(poseStack, consumer, playerPos, camX, camY, camZ, 1f, 0f, 0f, 1f);

        int oresFound = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-RADIUS, -RADIUS, -RADIUS),
                playerPos.offset(RADIUS, RADIUS, RADIUS))) {
            BlockState state = player.level().getBlockState(pos);
            Block block = state.getBlock();

            boolean isOre = block.builtInRegistryHolder().tags()
                    .anyMatch(tag -> tag.location().equals(VANILLA_ORES_TAG) || tag.location().equals(FORGE_ORES_TAG));

            if (isOre) {
                drawBox(poseStack, consumer, pos, camX, camY, camZ, 1f, 1f, 0f, 1f);
                oresFound++;
            }
        }

        System.out.println("[DEBUG] Total ores found: " + oresFound);

        buffer.endBatch(RenderType.lines());
    }

    private static void drawBox(PoseStack poseStack, VertexConsumer consumer, BlockPos pos, double camX, double camY, double camZ, float r, float g, float b, float a) {
        double x = pos.getX() - camX;
        double y = pos.getY() - camY;
        double z = pos.getZ() - camZ;
        float min = 0.002f;
        float max = 0.998f;

        float[][] points = {
                {min, min, min}, {max, min, min},
                {max, min, min}, {max, max, min},
                {max, max, min}, {min, max, min},
                {min, max, min}, {min, min, min},

                {min, min, max}, {max, min, max},
                {max, min, max}, {max, max, max},
                {max, max, max}, {min, max, max},
                {min, max, max}, {min, min, max},

                {min, min, min}, {min, min, max},
                {max, min, min}, {max, min, max},
                {max, max, min}, {max, max, max},
                {min, max, min}, {min, max, max}
        };

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        for (int i = 0; i < points.length; i += 2) {
            float[] p1 = points[i];
            float[] p2 = points[i + 1];
            consumer.vertex(poseStack.last().pose(), p1[0], p1[1], p1[2]).color(r, g, b, a).normal(1, 0, 0).endVertex();
            consumer.vertex(poseStack.last().pose(), p2[0], p2[1], p2[2]).color(r, g, b, a).normal(1, 0, 0).endVertex();
        }
        poseStack.popPose();
    }
}

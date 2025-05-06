package net.veri.wildwonders.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.veri.wildwonders.WildWonders;
import net.veri.wildwonders.item.ModItems;

@Mod.EventBusSubscriber(modid = WildWonders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.getMainHandItem().getItem() == ModItems.GUARDIAN_COMPASS.get()) {
            mc.level.getEntitiesOfClass(IronGolem.class,
                            new AABB(mc.player.blockPosition()).inflate(32))
                    .forEach(golem -> renderGolemZone(event, golem));
        }
    }

    private static void renderGolemZone(RenderLevelStageEvent event, IronGolem golem) {
        Minecraft mc = Minecraft.getInstance();
        CompoundTag data = golem.getPersistentData();

        if (!data.contains("wildwonders:home")) return;

        ListTag home = data.getList("wildwonders:home", Tag.TAG_DOUBLE);
        if (home.size() < 3) return;

        Vec3 center = new Vec3(
                home.getDouble(0),
                home.getDouble(1) + 0.5,
                home.getDouble(2)
        );

        int radius = 16;
        int particles = 72;
        RandomSource random = mc.level.random; // Use Minecraft's RandomSource

        // Add vertical marker particles
        for (int i = 0; i < 5; i++) {
            mc.level.addParticle(ParticleTypes.GLOW_SQUID_INK,
                    center.x,
                    center.y + i,
                    center.z,
                    random.nextGaussian() * 0.1,
                    0.1,
                    random.nextGaussian() * 0.1
            );
        }

        // Add rotating particles
        double time = (mc.level.getGameTime() % 360) * 0.5;
        for (int i = 0; i < particles; i++) {
            double angle = Math.toRadians(i * (360.0 / particles) + time);
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            mc.level.addParticle(ParticleTypes.END_ROD,
                    x, center.y, z,
                    random.nextGaussian() * 0.02,
                    random.nextDouble() * 0.1,
                    random.nextGaussian() * 0.02
            );
        }
        // Add rotating compass particles when holding item
        if (mc.player.isShiftKeyDown()) {
            mc.level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    mc.player.getX(),
                    mc.player.getY() + 1,
                    mc.player.getZ(),
                    0, 0.1, 0
            );
        }
    }
}
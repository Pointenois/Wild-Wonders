package net.veri.wildwonders.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModEffects {
    // Create the DeferredRegister for MobEffects
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "wildwonders");

    // Register your custom effect here
    public static final RegistryObject<MobEffect> SPELUNKER =
            EFFECTS.register("spelunker", SpelunkerEffect::new);

    // Call this in your mod's main class during setup
    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}

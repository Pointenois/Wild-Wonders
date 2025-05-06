package net.veri.wildwonders.potion;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.veri.wildwonders.effect.ModEffects;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, "wildwonders");

    public static final RegistryObject<Potion> SPELUNKER_POTION =
            POTIONS.register("spelunker_potion",
                    () -> new Potion(new MobEffectInstance(ModEffects.SPELUNKER.get(), 36000)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}

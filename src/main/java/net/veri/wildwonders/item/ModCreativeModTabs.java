package net.veri.wildwonders.item;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.veri.wildwonders.WildWonders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.veri.wildwonders.potion.ModPotions;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WildWonders.MOD_ID);

    public static final RegistryObject<CreativeModeTab> WILD_TAB = CREATIVE_MODE_TABS.register("wild_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SAPPHIRE.get()))
                    .title(Component.translatable("creativetab.wild_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SAPPHIRE.get());
                        pOutput.accept(ModItems.RAW_SAPPHIRE.get());
                        pOutput.accept(ModItems.BAT_WING.get());
                        pOutput.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SPELUNKER_POTION.get()));
                        pOutput.accept(ModItems.GUARDIAN_COMPASS.get());

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
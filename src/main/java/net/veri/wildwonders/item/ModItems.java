package net.veri.wildwonders.item;

import net.minecraft.world.item.Rarity;
import net.veri.wildwonders.WildWonders;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WildWonders.MOD_ID);

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_SAPPHIRE = ITEMS.register("raw_sapphire",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BAT_WING = ITEMS.register("bat_wing",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GUARDIAN_COMPASS = ITEMS.register("guardian_compass",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
                    .setNoRepair()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

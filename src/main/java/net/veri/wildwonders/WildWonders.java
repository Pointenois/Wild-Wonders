package net.veri.wildwonders;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.veri.wildwonders.effect.ModEffects;
import net.veri.wildwonders.entity.ai.ChaseBeyondHomeGoal;
import net.veri.wildwonders.entity.ai.ReturnToHomeGoal;
import net.veri.wildwonders.events.SpelunkerClientEvents;
import net.veri.wildwonders.item.ModCreativeModTabs;
import net.veri.wildwonders.item.ModItems;
import net.veri.wildwonders.potion.ModPotions;
import net.veri.wildwonders.potion.SpelunkerBrewingRecipe;
import org.slf4j.Logger;

@Mod(WildWonders.MOD_ID)
public class WildWonders {
    public static final String MOD_ID = "wildwonders";
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof IronGolem golem &&
                event.getItemStack().getItem() == ModItems.GUARDIAN_COMPASS.get()) {

            CompoundTag data = golem.getPersistentData();
            Player player = event.getEntity();

            if (player.isShiftKeyDown()) {
                ListTag newHome = new ListTag();
                newHome.add(DoubleTag.valueOf(golem.getX()));
                newHome.add(DoubleTag.valueOf(golem.getY()));
                newHome.add(DoubleTag.valueOf(golem.getZ()));
                data.put("wildwonders:home", newHome);

                if (!event.getLevel().isClientSide()) {
                    player.level().playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                            SoundEvents.NOTE_BLOCK_BELL.get(), SoundSource.NEUTRAL, 0.8F, 1.2F);

                    player.sendSystemMessage(Component.literal("Updated protection zone center!"));
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
            }
        }
    }
    public WildWonders() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModEffects.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModPotions.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(SpelunkerClientEvents.class);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(
                    new SpelunkerBrewingRecipe(
                            Potions.AWKWARD,
                            new ItemStack(ModItems.BAT_WING.get()),
                            ModPotions.SPELUNKER_POTION.get()
                    )
            );
        });
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof IronGolem golem && !event.getLevel().isClientSide()) {
            CompoundTag data = golem.getPersistentData();

            // Store home position
            if (!data.contains("wildwonders:home")) {
                ListTag homePos = new ListTag();
                homePos.add(DoubleTag.valueOf(golem.getX()));
                homePos.add(DoubleTag.valueOf(golem.getY()));
                homePos.add(DoubleTag.valueOf(golem.getZ()));
                data.put("wildwonders:home", homePos);
            }

            // Remove only the wandering behavior
            golem.goalSelector.getAvailableGoals().removeIf(goal ->
                    goal.getGoal() instanceof WaterAvoidingRandomStrollGoal
            );

            // Add custom AI with adjusted priorities
            golem.goalSelector.addGoal(2, new ReturnToHomeGoal(golem, 1.0D, 16, 2));

            golem.goalSelector.addGoal(3, new ChaseBeyondHomeGoal(golem, 1.2D, 32));

            // Enhanced targeting system
            golem.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(golem, Mob.class, 24, // Increased detection range
                    true, false, entity -> {
                ListTag home = data.getList("wildwonders:home", Tag.TAG_DOUBLE);
                Vec3 homeVec = new Vec3(home.getDouble(0), home.getDouble(1), home.getDouble(2));

                // Combined conditions with proper parentheses
                return (entity instanceof Enemy ||
                        entity.getType().getCategory() == MobCategory.MONSTER)
                        && entity.distanceToSqr(homeVec) <= 24 * 24;
            }));
        }
    }

    // Rest of the class remains unchanged
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.SAPPHIRE);
            event.accept(ModItems.RAW_SAPPHIRE);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == ModItems.GUARDIAN_COMPASS.get()) {
            event.getToolTip().add(Component.literal("§7Shift-right-click golems to update").withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.literal("§7their protection zone center").withStyle(ChatFormatting.GRAY));
        }
    }
}
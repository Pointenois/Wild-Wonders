package net.veri.wildwonders.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PetWanderHandler {
    public static final String WANDER_STATE_TAG = "wander_state";
    public enum WanderState { FOLLOW, STAY, WANDER }

    public static InteractionResult handlePetInteraction(TamableAnimal pet, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.STICK && pet.isTame() && pet.isOwnedBy(player)) {
            if (!pet.level().isClientSide()) {
                cycleWanderState(pet);
                sendFeedback(player, pet);
                playEffects(pet.level(), pet);
            }
            return InteractionResult.sidedSuccess(pet.level().isClientSide());
        }
        return InteractionResult.PASS;
    }

    private static void cycleWanderState(TamableAnimal pet) {
        CompoundTag data = pet.getPersistentData();
        int current = data.getInt(WANDER_STATE_TAG);
        int next = (current + 1) % WanderState.values().length;
        data.putInt(WANDER_STATE_TAG, next);
        updateAI(pet);
    }

    private static void updateAI(TamableAnimal pet) {
        pet.setOrderedToSit(getWanderState(pet) == WanderState.STAY);

        // Clear existing goals
        pet.goalSelector.getAvailableGoals().removeIf(goal ->
                goal.getGoal() instanceof OwnerHurtByTargetGoal ||
                        goal.getGoal() instanceof OwnerHurtTargetGoal ||
                        goal.getGoal() instanceof FollowOwnerGoal
        );

        // Add appropriate goals based on state
        switch (getWanderState(pet)) {
            case FOLLOW -> {
                pet.goalSelector.addGoal(3, new FollowOwnerGoal(pet, 1.0D, 10.0F, 2.0F, false));
                pet.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(pet));
                pet.targetSelector.addGoal(2, new OwnerHurtTargetGoal(pet));
            }
            case WANDER -> {
                pet.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(pet, 1.0D));
            }
        }
    }

    public static WanderState getWanderState(TamableAnimal pet) {
        int state = pet.getPersistentData().getInt(WANDER_STATE_TAG);
        return WanderState.values()[Math.max(0, state)];
    }

    private static void sendFeedback(Player player, TamableAnimal pet) {
        Component petName = pet.getDisplayName();
        WanderState state = getWanderState(pet);

        MutableComponent message = Component.empty()
                .append(petName)
                .append(" ")
                .append(Component.translatable("msg.wildwonders.pet_mode." + state.name().toLowerCase()));

        player.displayClientMessage(message, true);
    }

    private static void playEffects(Level level, TamableAnimal pet) {
        level.playSound(null, pet.getX(), pet.getY(), pet.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.NEUTRAL,
                0.7F,
                1.5F);

    }
}
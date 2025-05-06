package net.veri.wildwonders.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class SpelunkerBrewingRecipe implements IBrewingRecipe {
    private final Potion inputPotion;
    private final ItemStack ingredient;
    private final Potion outputPotion;

    public SpelunkerBrewingRecipe(Potion inputPotion, ItemStack ingredient, Potion outputPotion) {
        this.inputPotion = inputPotion;
        this.ingredient = ingredient;
        this.outputPotion = outputPotion;
    }

    @Override
    public boolean isInput(ItemStack input) {
        return input.getItem() == Items.POTION && PotionUtils.getPotion(input) == inputPotion;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ItemStack.isSameItemSameTags(this.ingredient, ingredient);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (isInput(input) && isIngredient(ingredient)) {
            return PotionUtils.setPotion(new ItemStack(Items.POTION), outputPotion);
        }
        return ItemStack.EMPTY;
    }
}

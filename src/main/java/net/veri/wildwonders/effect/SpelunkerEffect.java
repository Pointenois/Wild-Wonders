package net.veri.wildwonders.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SpelunkerEffect extends MobEffect {
    public SpelunkerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700); // Gold color
    }

    // No server-side logic needed here; client-side rendering handles the highlight
}

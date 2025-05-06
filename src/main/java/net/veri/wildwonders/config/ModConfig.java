package net.veri.wildwonders.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue PROTECTION_RADIUS;

    static {
        BUILDER.push("Wild Wonders Config");

        PROTECTION_RADIUS = BUILDER
                .comment("Radius of Iron Golem protection zone (8-64 blocks)")
                .defineInRange("protectionRadius", 16, 8, 64);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
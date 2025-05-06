package net.veri.wildwonders.entity.ai;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ReturnToHomeGoal extends Goal {
    private final IronGolem golem;
    private final double speed;
    private final int radius;
    private Vec3 homePos;
    private int tickCooldown;

    public ReturnToHomeGoal(IronGolem golem, double speed, int radius, int cooldown) {
        this.golem = golem;
        this.speed = speed;
        this.radius = radius;
        this.tickCooldown = cooldown;
    }

    @Override
    public boolean canUse() {
        // Don't activate if in combat
        if (golem.getTarget() != null) return false;

        if (tickCooldown > 0) {
            tickCooldown--;
            return false;
        }
        tickCooldown = 2;

        ListTag home = golem.getPersistentData().getList("wildwonders:home", Tag.TAG_DOUBLE);
        if (home.size() < 3) return false;
        homePos = new Vec3(home.getDouble(0), home.getDouble(1), home.getDouble(2));

        return golem.distanceToSqr(homePos) > radius * radius;
    }

    @Override
    public boolean canContinueToUse() {
        // Keep returning home even if interrupted
        return canUse() || !golem.getNavigation().isDone();
    }

    @Override
    public void tick() {
        // Re-pathfind if stuck
        if (golem.getNavigation().isDone() && homePos != null) {
            golem.getNavigation().moveTo(homePos.x, homePos.y, homePos.z, speed);
        }
    }
}
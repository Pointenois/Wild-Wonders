package net.veri.wildwonders.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;

public class ChaseBeyondHomeGoal extends Goal {
    private final IronGolem golem;
    private final double speed;
    private final int maxChaseDistance;

    public ChaseBeyondHomeGoal(IronGolem golem, double speed, int maxChaseDistance) {
        this.golem = golem;
        this.speed = speed;
        this.maxChaseDistance = maxChaseDistance;
    }

    @Override
    public boolean canUse() {
        return golem.getTarget() != null &&
                golem.distanceToSqr(golem.getTarget()) < maxChaseDistance * maxChaseDistance;
    }

    @Override
    public void start() {
        golem.getNavigation().moveTo(golem.getTarget(), speed);
    }

    @Override
    public void stop() {
        golem.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
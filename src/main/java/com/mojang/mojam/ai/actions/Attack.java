package com.mojang.mojam.ai.actions;

import com.mojang.mojam.Team;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Humanoid;

public abstract class Attack extends OpposingEntityInVicinityAction {
    private final long cooldown;
    private boolean executing;
    private long lastAttack;

    public Attack(Team opposingTeam, float range, long cooldownMs) {
        super(opposingTeam, range);
        this.executing = false;
        this.cooldown = cooldownMs;
    }

    @Override
    public void performAction(Humanoid entity) {
        if (!shouldAnimateAttack() && isOnCooldown()) return;
        if (!executing && (!shouldAnimateAttack() || entity.isReadyForAttack())) {
            executing = true;
            lastAttack = System.currentTimeMillis();
            entity.setHorizontalSpeed(0);
            Entity target = getNearestEnemy(entity);

            if (target != null) {
                if (target.getPosition().getMinX() < entity.getPosition().getX()) {
                    entity.setFacing(Entity.FACING_LEFT);
                } else {
                    entity.setFacing(Entity.FACING_RIGHT);
                }
            }

            performAttack(entity);
        }
    }

    protected abstract void performAttack(Humanoid entity);

    @Override
    public boolean isConditionMet(Humanoid entity) {
        return !isOnCooldown() && super.isConditionMet(entity);
    }

    private boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastAttack) < cooldown;
    }

    public void setExecuting(boolean executing) {
        this.executing = executing;
    }

    public boolean shouldAnimateAttack() {
        return true;
    }
}

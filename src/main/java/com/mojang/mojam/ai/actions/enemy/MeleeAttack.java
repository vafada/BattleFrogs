package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.Team;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Humanoid;
import org.newdawn.slick.SlickException;

public class MeleeAttack extends Attack {

    private static final int COOLDOWN_MS = 100;
    private static final int RANGE = 0;

    private final int damage;

    public MeleeAttack(Team opposingTeam, int damage) {
        super(opposingTeam, RANGE, COOLDOWN_MS);

        this.damage = damage;
    }

    @Override
    protected void performAttack(Humanoid entity) {
        Entity target = getNearestEnemy(entity);

        if (target != null) {
            try {
                target.decreaseHealth(damage);
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }

        setExecuting(false);
    }

    @Override
    public boolean isConditionMet(Humanoid entity) {
        return isEnemyInRange(entity);
    }

    @Override
    public boolean shouldAnimateAttack() {
        return false;
    }
}

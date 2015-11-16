package com.mojang.mojam.entities;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.ai.Behavior;
import com.mojang.mojam.ai.actions.Action;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.ai.actions.enemy.Patrol;
import com.mojang.mojam.ai.actions.enemy.ReturnToGuardPoint;
import com.mojang.mojam.entities.projectiles.Missile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public abstract class Enemy extends Humanoid {
    protected boolean isAttacking = false;
    protected boolean isReadyForAttack = false;
    protected boolean hasAttackHappened = false;
    protected boolean wasAttacking = false;
    protected long attackStart = 0;

    public Enemy(World world, Image image, Point point, int width, int height) {
        super(world, image, point, width, height, Team.THE_FROG_PIRATES);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        wasAttacking = isAttacking;
        boolean cycledAttack = false;
        long attackDelta = gameContainer.getTime() - attackStart;
        isReadyForAttack = false;

        if (isAttacking && attackDelta >= getAttackTime()) {
            isAttacking = false;
            cycledAttack = true;
        }

        if (!wasJumping && !isJumping && !isAttacking && canAttack()) {
            attackStart = gameContainer.getTime();
            isAttacking = true;
            hasAttackHappened = false;
            attackDelta = 0;
        }

        if (isAttacking && !hasAttackHappened && attackDelta >= getAttackOffsetTime()) {
            isReadyForAttack = true;
            hasAttackHappened = true;
        }

        if (wasAttacking && !isAttacking) {
            stopAttackAnimation();
        } else if (cycledAttack && isAttacking) {
            cycleAttackAnimation();
        }

        super.update(gameContainer, deltaTime);
    }

    @Override
    public boolean isReadyForAttack() {
        return isReadyForAttack;
    }

    protected abstract void cycleAttackAnimation();

    protected abstract void stopAttackAnimation();

    protected abstract long getAttackOffsetTime();

    protected boolean canAttack() {
        for (Behavior behavior : behaviors) {
            Action action = behavior.getAction();
            if (action instanceof Attack && ((Attack) action).shouldAnimateAttack() && action.isConditionMet(this)) return true;
        }

        return false;
    }

    protected abstract long getAttackTime();
}

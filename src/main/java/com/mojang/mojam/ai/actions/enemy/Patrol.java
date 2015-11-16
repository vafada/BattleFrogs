package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.ai.actions.Action;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Humanoid;

public class Patrol extends Action {
    private final float patrolLength;
    private final int patrolSpeed;

    public Patrol(int patrolSpeed, float patrolLength) {
        this.patrolLength = patrolLength;
        this.patrolSpeed = patrolSpeed;
    }

    @Override
    public void performAction(Humanoid entity) {
        if (Math.abs(entity.getHorizontalSpeed()) != patrolSpeed) {
            resumePatrol(entity);
        } else {
            if (hasReachedRightEnd(entity) || hasReachedLeftEnd(entity)) {
                entity.invertHorizontalSpeed();
            }
        }
    }

    private void resumePatrol(Humanoid entity) {
        entity.setHorizontalSpeed(patrolSpeed);
        if (entity.getFacing() == Entity.FACING_LEFT) {
            entity.invertHorizontalSpeed();
        }
    }

    private boolean hasReachedLeftEnd(Humanoid entity) {
        if (entity.getFacing() == Entity.FACING_LEFT) {
            float leftPatrolEndPoint = entity.getStartingPoint().getX() - patrolLength;
            boolean hasVenturedOutsidePatrolArea = entity.getPosition().getCenterX() < leftPatrolEndPoint;
            return entity.isMovingLeft() && (!entity.canContinueMoving() || hasVenturedOutsidePatrolArea);
        }
        return false;
    }

    private boolean hasReachedRightEnd(Humanoid entity) {
        if (entity.getFacing() == Entity.FACING_RIGHT) {
            float rightPatrolEndPoint = entity.getStartingPoint().getX() + patrolLength;
            boolean hasVenturedOutsidePatrolArea = entity.getPosition().getCenterX() > rightPatrolEndPoint;
            return entity.isMovingRight() && (!entity.canContinueMoving() || hasVenturedOutsidePatrolArea);
        }
        return false;
    }

    @Override
    public boolean isConditionMet(Humanoid entity) {
        return true;
    }
}

package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.ai.actions.Action;
import com.mojang.mojam.entities.Humanoid;
import org.newdawn.slick.geom.Point;

public class ReturnToGuardPoint extends Action {

    private static final int MAX_GUARDING_POINT_DISTANCE = 600;
    private final int returningSpeed;
    private final Point guardPoint;

    public ReturnToGuardPoint(Point guardPoint, int returningSpeed) {
        this.guardPoint = guardPoint;
        this.returningSpeed = returningSpeed;
    }

    @Override
    public void performAction(Humanoid entity) {
        entity.setHorizontalSpeed(returningSpeed);
        if (isRightOfStartingPoint(entity)) {
            entity.moveLeft();
        } else {
            entity.moveRight();
        }
    }

    private boolean isRightOfStartingPoint(Humanoid entity) {
        return entity.getPosition().getMinX() > guardPoint.getX();
    }

    @Override
    public boolean isConditionMet(Humanoid entity) {
        return entity.deltaX(guardPoint) > MAX_GUARDING_POINT_DISTANCE;
    }
}

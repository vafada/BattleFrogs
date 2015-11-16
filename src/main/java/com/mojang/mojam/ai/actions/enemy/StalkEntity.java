package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.Team;
import com.mojang.mojam.ai.actions.OpposingEntityInVicinityAction;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Humanoid;

public class StalkEntity extends OpposingEntityInVicinityAction {

    public static final int RANGE = 300;
    private final int stalkingSpeed;

    public StalkEntity(Team opposingTeam, int stalkingSpeed) {
        super(opposingTeam, RANGE);
        this.stalkingSpeed = stalkingSpeed;
    }

    @Override
    public void performAction(Humanoid entity) {
        if (Math.abs(entity.getHorizontalSpeed()) != stalkingSpeed) {
            resumeStalking(entity);
        }

        Entity target = getNearestEnemy(entity);

        if (target != null) {
            if (entity.getPosition().getX() > target.getPosition().getX()) {
                entity.moveLeft();
            } else if (entity.getPosition().getX() < target.getPosition().getX()) {
                entity.moveRight();
            }
        }
    }

    private void resumeStalking(Humanoid entity) {
        entity.setHorizontalSpeed(stalkingSpeed);
        if (entity.getFacing() == Entity.FACING_LEFT) {
            entity.invertHorizontalSpeed();
        }
    }
}

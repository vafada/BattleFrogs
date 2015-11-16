package com.mojang.mojam.ai.actions;

import com.mojang.mojam.Team;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Humanoid;
import org.newdawn.slick.geom.Vector2f;

public abstract class OpposingEntityInVicinityAction extends Action {

    private final float range;
    private final Team opposingTeam;

    // TODO: Rewrite this to use teams instead.

    public OpposingEntityInVicinityAction(Team opposingTeam, float range) {
        this.opposingTeam = opposingTeam;
        this.range = range;
    }

    @Override
    public boolean isConditionMet(Humanoid entity) {
        return isEnemyInRange(entity);
    }

    protected boolean isEnemyInRange(Humanoid entity) {
        Entity opposingEntity = getNearestEnemy(entity);
        if (opposingEntity == null) return false;

        boolean intersecting = entity.isIntersecting(opposingEntity);
        float deltaY = entity.deltaY(opposingEntity);
        boolean inRange = entity.deltaX(opposingEntity) <= range;
        boolean inLineOfSight = (deltaY == 0) && inRange;
        return intersecting || inLineOfSight;
    }

    public Entity getNearestEnemy(Humanoid entity) {
         return entity.getWorld().getNearestEntity(new Vector2f(entity.getPosition().getCenter()), opposingTeam);
    }

    public Team getOpposingTeam() {
        return opposingTeam;
    }
}

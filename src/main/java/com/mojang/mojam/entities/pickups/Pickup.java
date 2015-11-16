package com.mojang.mojam.entities.pickups;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.entities.Entity;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Shape;

import java.util.List;

public abstract class Pickup extends Entity {

    public Pickup(World world, Image image, Point startingPoint, int width, int height, Team team) {
        super(world, image, startingPoint, width, height, team);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        move();

        List<Entity> collidingEntities = world.getCollidingEntities(getPickupHitbox());
        for (Entity entity : collidingEntities) {
            if (entity != this && isEligible(entity)) {
                applyEffect(entity);
                die();
                break;
            }
        }
    }

    protected Shape getPickupHitbox() {
        return position;
    }

    protected boolean isEligible(Entity entity) {
        return entity.getTeam().equals(team);
    }

    @Override
    public void decreaseHealth(int amount) throws SlickException {
        // Do nothing. Doesn't decrease in health.
    }

    protected abstract void applyEffect(Entity entity);
}

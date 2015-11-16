package com.mojang.mojam.entities.obstacles;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.entities.Entity;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;

public abstract class Obstacle extends Entity {

    private Rectangle collisionBox;

    protected Obstacle(World world, Image image, Point startingPoint, int width, int height, Team team, Rectangle collisionBox) {
        super(world, image, startingPoint, width, height, team);
        world.addObstacle(this);
        this.collisionBox = collisionBox;
    }

    @Override
    protected void die() throws SlickException {
        super.die();
        world.removeObstacle(this);
    }

    public Rectangle getCollisionHitbox() {
        return collisionBox;
    }
}

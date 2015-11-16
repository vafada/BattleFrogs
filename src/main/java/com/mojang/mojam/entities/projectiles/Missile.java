package com.mojang.mojam.entities.projectiles;

import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Explosion;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class Missile extends Projectile {
    public static final int RANGE = 500;
    public static final int DAMAGE = 25;
    public static final int SPEED = 20;

    public Missile(Entity origin, Attack attack) throws SlickException {
        super(new Image("graphics/missile.png"), origin, attack, 60, 29, RANGE, DAMAGE);
        horizontalSpeed = facing == FACING_RIGHT ? SPEED : -SPEED;
    }

    @Override
    protected void die() throws SlickException {
        super.die();
        getAttack().setExecuting(false);

        Point point = new Point(facing == FACING_LEFT ? position.getMinX() : position.getMaxX(), position.getCenterY());
        Explosion explosion = new Explosion(world, point, Explosion.TYPE_FROMAGE);
        world.addEntity(explosion);
    }

    @Override
    protected int getImageX0(Image image) {
        return facing == FACING_LEFT ? image.getWidth() : 0;
    }

    @Override
    protected int getImageX1(Image image) {
        return facing == FACING_LEFT ? 0 : image.getWidth();
    }

    protected Point getProjectilePoint(Entity entity) {
        if (entity.getFacing() == Entity.FACING_RIGHT) {
            return new Point(entity.getPosition().getMaxX(), entity.getPosition().getCenterY() - 23);
        } else {
            return new Point(entity.getPosition().getMinX() - position.getWidth(), entity.getPosition().getCenterY() - 23);
        }
    }
}

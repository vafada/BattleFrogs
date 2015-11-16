package com.mojang.mojam.entities.projectiles;

import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.ai.actions.enemy.TadpoleAttack;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Explosion;
import com.mojang.mojam.entities.FrogBoss;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class Tadpole extends Projectile {
    public static final int RANGE = TadpoleAttack.RANGE;
    public static final int DAMAGE = 25;
    public static final int SPEED = 20;

    public Tadpole(Entity origin, Attack attack) throws SlickException {
        super(new Image("graphics/tadpole.png"), origin, attack, 70, 49, RANGE, DAMAGE);
        horizontalSpeed = facing == FACING_RIGHT ? SPEED : -SPEED;

        Point point = new Point(facing == FACING_LEFT ? position.getMinX() : position.getMaxX(), position.getCenterY());
        Explosion explosion = new Explosion(world, point, Explosion.TYPE_TADPOLE);
        world.addEntity(explosion);
    }

    @Override
    protected void die() throws SlickException {
        super.die();
        getAttack().setExecuting(false);

        Point point = new Point(facing == FACING_LEFT ? position.getMinX() : position.getMaxX(), position.getCenterY());
        Explosion explosion = new Explosion(world, point, Explosion.TYPE_TADPOLE_SPLATTER);
        explosion.setFacing(facing == FACING_LEFT ? FACING_RIGHT : FACING_LEFT);
        world.addEntity(explosion);
    }

    protected Point getProjectilePoint(Entity entity) {
        Point point = FrogBoss.PROJECTILE_COORDINATES[((FrogBoss) entity).getMissileIndex()];

        if (entity.getFacing() == Entity.FACING_RIGHT) {
            return new Point(entity.getPosition().getX() + point.getX(), entity.getPosition().getY() + point.getY());
        } else {
            return new Point(entity.getPosition().getMaxX() - point.getX(), entity.getPosition().getMaxY() - point.getY());
        }
    }
}

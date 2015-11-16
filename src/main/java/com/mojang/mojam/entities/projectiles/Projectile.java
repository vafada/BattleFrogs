package com.mojang.mojam.entities.projectiles;

import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.entities.Enemy;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.obstacles.Obstacle;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Point;

public class Projectile extends Entity {

    private final Sound frogHitFx;

    private final float range;
    private final int damage;
    private final Attack attack;

    public Projectile(Image image, Entity origin, Attack attack, int width, int height, float range, int damage) throws SlickException {
        super(origin.getWorld(), image, new Point(origin.getPosition().getCenterX(), origin.getPosition().getCenterY()), width, height, origin.getTeam());
        this.damageModifier = origin.getDamageModifier();
        this.startingPoint = getProjectilePoint(origin);
        this.position.setX(startingPoint.getX());
        this.position.setY(startingPoint.getY());
        this.range = range;
        this.damage = damage;
        this.attack = attack;

        facing = origin.getFacing();
        flying = true;

        frogHitFx = new Sound("sounds/Frog_Exploding.wav");
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        velocity.x += horizontalSpeed;
        move();

        if ((gameContainer.getTime() - lifeStart) >= getMaxLifetime()) {
            die();
            return;
        }

        for (Entity entity : world.getCollidingEntities(this)) {
            if (entity.getTeam() != getTeam()) {
                dealDamage(entity);

                if (entity instanceof Enemy) {
                    SoundManager.getInstance().playSoundEffect(frogHitFx);
                }

                die();
                return;
            }
        }

        if ((facing == FACING_RIGHT) && (position.getMinX() >= (startingPoint.getX() + range))) {
            die();
        } else if ((facing == FACING_LEFT) && (position.getMaxX() <= (startingPoint.getX() - range))) {
            die();
        }
    }

    @Override
    protected void onCollision(boolean collidedHorizontally, boolean collidedVertically) throws SlickException {
        die();
    }

    @Override
    public void onObstacleCollision(Obstacle obstacle) {
        if (obstacle.getTeam() != team) {
            try {
                dealDamage(obstacle);
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
    }

    private void dealDamage(Entity entity) throws SlickException {
        entity.decreaseHealth((int) (damage * getDamageModifier()));
    }

    protected Point getProjectilePoint(Entity entity) {
        if (entity.getFacing() == Entity.FACING_RIGHT) {
            return new Point(entity.getPosition().getMaxX(), entity.getPosition().getCenterY());
        } else {
            return new Point(entity.getPosition().getMinX() - position.getWidth(), entity.getPosition().getCenterY());
        }
    }

    public Attack getAttack() {
        return attack;
    }

    public long getMaxLifetime() {
        return 5 * 1000;
    }

    @Override
    public boolean ignoreCollision() {
        return true;
    }
}

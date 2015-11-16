package com.mojang.mojam.entities;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.entities.obstacles.Obstacle;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public abstract class Entity {
    public static final int FACING_LEFT = 0;
    public static final int FACING_RIGHT = 1;

    protected final World world;
    protected final SpriteSheet animationSheet;
    protected final Random random = new Random();
    protected Point startingPoint;
    protected Rectangle frameSize;
    protected final Rectangle position;
    protected Vector2f velocity = new Vector2f();
    protected float horizontalSpeed = 0;
    protected float gravity = 0.75f;
    protected float jumpSpeed = 30f * gravity;
    protected float friction = 0.35f;
    protected boolean isMoving = false;
    protected boolean isJumping = false;
    protected boolean flying = false;
    protected int frame = 0;
    protected long lifeStart = 0;
    protected int facing = 0;
    protected boolean wasJumping = false;
    protected boolean wasMoving = false;
    protected Team team;

    protected int fullHealth = 100;
    protected int currentHealth = fullHealth;
    protected float damageModifier = 1;
    private boolean invulnerable;

    public Entity(World world, Image image, Point startingPoint, int width, int height, Team team) {
        this.world = world;
        this.startingPoint = startingPoint;
        this.position = new Rectangle(startingPoint.getX(), startingPoint.getY(), width, height);
        this.frameSize = new Rectangle(startingPoint.getX(), startingPoint.getY(), width, height);
        this.animationSheet = new SpriteSheet(image, width, height);
        this.team = team;
        this.lifeStart = world.getGame().getGameContainer().getTime();
    }

    public abstract void update(GameContainer gameContainer, int deltaTime) throws SlickException;

    protected void move() throws SlickException {
        velocity.x *= friction;
        if (Math.abs(velocity.x) < friction) {
            velocity.x = 0;
        }

        if (isJumping) {
            velocity.y += gravity;
        } else {
            velocity.y = 0;
        }

        float newX = position.getX() + velocity.x;
        float newY = position.getY() + velocity.y;

        if (newY > (World.FLOOR_LEVEL - position.getHeight())) {
            newY = World.FLOOR_LEVEL - position.getHeight();
        }

        boolean collidedHorizontally = false;
        boolean collidedVertically = false;
        boolean onFloor = false;

        if (!world.isCollision(this, getCollisionHitbox(new Rectangle(newX, position.getY(), position.getWidth(), position.getHeight())), false)) {
            position.setLocation(newX, position.getY());
        } else {
            velocity.x = 0;
            collidedHorizontally = true;
        }

        if (velocity.y < 0) {
            if (!world.isCollision(this, getCollisionHitbox(new Rectangle(position.getX(), newY, position.getWidth(), position.getHeight())), false)) {
                position.setLocation(position.getX(), newY);
            } else {
                collidedVertically = true;
            }
        } else {
            if (!world.isCollision(this, getCollisionHitbox(new Rectangle(position.getMinX(), getPosition().getMaxY(), position.getWidth(), Math.max(velocity.y, gravity))), true)) {
                position.setLocation(position.getX(), newY);
            } else {
                velocity.y = 0;
                collidedVertically = true;
                onFloor = true;
            }
        }

        wasJumping = isJumping;
        wasMoving = isMoving;
        isJumping = !flying && getPosition().getY() < World.FLOOR_LEVEL - getPosition().getHeight() && !onFloor;
        isMoving = Math.abs(velocity.x) > 0;

        if (wasJumping && !isJumping) {
            onLanding();
        }

        updateAnimation();

        if (velocity.x > 0) {
            facing = FACING_RIGHT;
        } else if (velocity.x < 0) {
            facing = FACING_LEFT;
        }

        if (collidedHorizontally || collidedVertically) onCollision(collidedHorizontally, collidedVertically);
    }

    protected void onLanding() {
    }

    protected void onCollision(boolean collidedHorizontally, boolean collidedVertically) throws SlickException {
    }

    protected void updateAnimation() {
    }

    public Rectangle getCollisionHitbox(Rectangle position) {
        return position;
    }

    public void render(Camera camera, GameContainer gameContainer) {
        frame++;

        Image image = getFrame();
        float x0 = getImageX0(image);
        float x1 = getImageX1(image);
        float drawOffset = 0;

        if (facing == FACING_LEFT) {
            drawOffset = position.getWidth() - image.getWidth();
        }

        image.draw(position.getX() + drawOffset, position.getY(), x0, 0, x1, image.getHeight());

        if (BattleToads.ALLOW_DEBUGGING) gameContainer.getGraphics().drawRect(position.getX(), position.getY(), frameSize.getWidth(), frameSize.getHeight());
    }

    protected int getImageX1(Image image) {
        return facing == FACING_LEFT ? image.getWidth() : 0;
    }

    protected int getImageX0(Image image) {
        return facing == FACING_LEFT ? 0 : image.getWidth();
    }

    protected Image getFrame() {
        return animationSheet.getSprite(0, 0);
    }

    protected void cycleAnimationFrame() {
    }

    public boolean isIntersecting(Entity otherEntity) {
        return position.intersects(otherEntity.position);
    }

    public float deltaX(Entity otherEntity) {
        boolean rightAfterLeft = position.getMaxX() >= otherEntity.getPosition().getMinX();
        boolean rightBeforeRight = position.getMaxX() <= otherEntity.getPosition().getMaxX();
        boolean rightIntersects = rightAfterLeft && rightBeforeRight;

        boolean leftBeforeRight = position.getMinX() <= otherEntity.getPosition().getMaxX();
        boolean leftBeforeLeft = position.getMinX() >= otherEntity.getPosition().getMinX();
        boolean leftIntersects = leftBeforeRight && leftBeforeLeft;

        boolean contained = leftBeforeLeft && rightBeforeRight;
        if (rightIntersects || leftIntersects || contained) {
            return 0;
        }

        if (position.getMaxX() < otherEntity.position.getMinX()) {
            return Math.abs(otherEntity.position.getMinX() - position.getMaxX());
        }

        return Math.abs(position.getMinX() - otherEntity.position.getMaxX());
    }

    public float deltaX(Point point) {
        if (position.contains(point.getX(), point.getY())) {
            return 0;
        }

        if (position.getMaxX() < point.getX()) {
            return Math.abs(point.getX() - position.getMaxX());
        }

        return Math.abs(position.getMinX() - point.getX());
    }

    public float deltaY(Entity otherEntity) {
        return deltaY(otherEntity.getPosition());
    }

    public float deltaY(Rectangle rectangle) {
        boolean topBelowTop = position.getMinY() >= rectangle.getMinY();
        boolean topAboveBottom = position.getMinY() <= rectangle.getMaxY();
        boolean topIntersects = topBelowTop && topAboveBottom;

        boolean bottomBelowTop = position.getMaxY() >= rectangle.getMinY();
        boolean bottomAboveBottom = position.getMaxY() <= rectangle.getMaxY();
        boolean bottomIntersects = bottomBelowTop && bottomAboveBottom;

        boolean contained = topBelowTop && bottomAboveBottom;

        if (topIntersects || bottomIntersects || contained) {
            return 0;
        }

        if (position.getMinY() < rectangle.getMaxY()) {
            return Math.abs(rectangle.getMaxY() - position.getMinY());
        }

        return Math.abs(position.getMaxY() - rectangle.getMinY());
    }

    public boolean canContinueMoving() {
        float newX = position.getX() + velocity.x;
        return !world.isCollision(this, getCollisionHitbox(new Rectangle(newX, position.getY(), position.getWidth(), position.getHeight())), false);
    }

    public void invertHorizontalSpeed() {
        horizontalSpeed *= -1;
    }

    public boolean isMovingRight() {
        return horizontalSpeed > 1;
    }

    public boolean isMovingLeft() {
        return horizontalSpeed < 1;
    }

    public void moveLeft() {
        horizontalSpeed = -Math.abs(horizontalSpeed);
    }

    public void moveRight() {
        horizontalSpeed = Math.abs(horizontalSpeed);
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public void increaseHealth(int amount) {
        System.out.println("Increased health: " + amount);
        currentHealth += amount;

        if (currentHealth > fullHealth) {
            currentHealth = fullHealth;
        }
    }

    public void decreaseHealth(int amount) throws SlickException {
        if (invulnerable) amount = 0;
        currentHealth -= amount;

        if (currentHealth <= 0) {
            currentHealth = 0;
            die();
        }
    }

    protected void die() throws SlickException {
        world.removeEntity(this);
    }

    public World getWorld() {
        return world;
    }

    public int getFacing() {
        return facing;
    }

    public Team getTeam() {
        return team;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public int getHealthMax() {
        return fullHealth;
    }

    public int getHealth() {
        return currentHealth;
    }

    public Rectangle getPosition() {
        return position;
    }

    public float getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(int horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public boolean ignoreCollision() {
        return false;
    }

    public void setFullHealth(int fullHealth) {
        this.fullHealth = fullHealth;
        if (currentHealth > fullHealth) {
            currentHealth = fullHealth;
        }
    }

    public void radiate(Reactor reactor, float radiation) throws SlickException {
    }

    public void onObstacleCollision(Obstacle obstacle) {
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setDamageModifier(float dmg) {
        this.damageModifier = dmg;
    }

    public float getDamageModifier() {
        return damageModifier;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
}

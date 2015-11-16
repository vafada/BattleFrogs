package com.mojang.mojam;

import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.FrogPirate;
import com.mojang.mojam.entities.Humanoid;
import com.mojang.mojam.entities.Player;
import com.mojang.mojam.entities.obstacles.Obstacle;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    public static final int FLOOR_LEVEL = 672;
    public static final int[] BACKGROUND_WIDTHS = new int[] {
            2000, 2000, 2000, 2000, 2000, 2000, 2000, 709
    };

    private final Image[] background;
    private final Vector2f size;
    private final List<Entity> entities = new ArrayList<Entity>();
    private final List<Rectangle> collisions = new ArrayList<Rectangle>();
    private static final double SPAWN_RATE_VARIANCE = 30 * 60 * 1000;
    private List<Obstacle> obstacles = new ArrayList<Obstacle>();
    private List<ForegroundObject> foregroundObjects = new ArrayList<ForegroundObject>();
    private final BattleToads game;
    private int numberOfBosses = 0;

    private final Image starBackground;

    private EasterEgg easterEgg;

    private State state = State.INTRO;

    private long lastEasterEggSpawn = 0;
    private long maxEasterEggInterval = 1000 * 15;

    private final Random random = new Random(System.currentTimeMillis());

    public enum State {
        INTRO, WEAPON_PICKED_UP, GAME_OVER, CRYO_DOOR_BLOWN, RADIATION_CLEARED, ENGINES_ON, WIN;
    }

    public World(BattleToads game, Vector2f size) throws SlickException {
        this.game = game;
        this.background = new Image[BACKGROUND_WIDTHS.length];
        this.starBackground = new Image("graphics/starbackground.png");
        this.size = size;

        for (int i = 0; i < background.length; i++) {
            background[i] = new Image("graphics/background_" + i + ".png");
        }

        setNextEasterEggSpawn(System.currentTimeMillis());
    }

    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        List<Entity> clonedEntities = new ArrayList<Entity>(entities);
        for (Entity entity : clonedEntities) {
            entity.update(gameContainer, deltaTime);
        }

        long now = System.currentTimeMillis();
        if ((now - maxEasterEggInterval) > lastEasterEggSpawn) {
            int r = random.nextInt(2);
            if (r == 0) {
                easterEgg = new SpaceKitten(this);
            } else if (r == 1) {
                easterEgg = new SpacePizza(this);
            }
            setNextEasterEggSpawn(now);
        }

        if (easterEgg != null) {
            easterEgg.update(deltaTime);
        }
    }

    private void setNextEasterEggSpawn(long now) {
        double variance = (random.nextGaussian() * SPAWN_RATE_VARIANCE * 2) - SPAWN_RATE_VARIANCE;
        lastEasterEggSpawn = now + (int) variance;
    }

    public void render(GameContainer gameContainer, Camera camera) throws SlickException {
        starBackground.draw(camera.getX(), 0, 1801, 540);

        if (easterEgg != null) {
            easterEgg.render(gameContainer, camera);
        }

        int bgx = 0;
        for (int i = 0; i < background.length; i++) {
            background[i].draw(bgx, 0);
            bgx += BACKGROUND_WIDTHS[i];
        }

        if (BattleToads.ALLOW_DEBUGGING && gameContainer.getInput().isKeyDown(Input.KEY_F1)) {
            for (Rectangle rect : collisions) {
                gameContainer.getGraphics().setColor(Color.blue);
                gameContainer.getGraphics().drawRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
            }
        }

        for (Entity entity : entities) {
            if (BattleToads.ALLOW_DEBUGGING) gameContainer.getGraphics().setColor(Color.pink);
            entity.render(camera, gameContainer);
        }

        for (ForegroundObject image : foregroundObjects) {
            image.render(camera, gameContainer);
        }
    }

    public void addCollision(Rectangle rect) {
        collisions.add(rect);
    }

    public boolean isCollision(Entity entity, Rectangle entityCollisionBox, boolean forGravity) {
        for (Rectangle collidable : collisions) {
            if (collidable.getHeight() == 0 && !forGravity) continue;

            if (entityCollisionBox.intersects(collidable)) {
                return true;
            }
        }

        for (Obstacle obstacle : obstacles) {
            if (entityCollisionBox.intersects(obstacle.getCollisionHitbox())) {
                entity.onObstacleCollision(obstacle);
                return true;
            }
        }

        return false;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public void removeObstacle(Obstacle obstacle) {
        obstacles.remove(obstacle);
    }

    public void addForegroundObject(ForegroundObject foregroundObject) {
        foregroundObjects.add(foregroundObject);
    }

    public void removeForegroundObject(ForegroundObject foregroundObject) {
        foregroundObjects.remove(foregroundObject);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public Vector2f getSize() {
        return size;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getCollidingEntities(Entity entity) {
        List<Entity> collisions = new ArrayList<Entity>();
        for (Entity otherEntity : entities) {
            if (!otherEntity.equals(entity) && !otherEntity.ignoreCollision()) {
                if (entity.getPosition().intersects(otherEntity.getPosition())) {
                    collisions.add(otherEntity);
                }
            }
        }
        return collisions;
    }

    public List<Entity> getCollidingEntities(Shape shape) {
        List<Entity> collisions = new ArrayList<Entity>();
        for (Entity otherEntity : entities) {
            if (!otherEntity.ignoreCollision()) {
                if (shape.intersects(otherEntity.getPosition())) {
                    collisions.add(otherEntity);
                }
            }
        }
        return collisions;
    }

    public BattleToads getGame() {
        return game;
    }

    public Entity getNearestEntity(Vector2f target, Team team) {
        Entity result = null;
        float bestDist = 0;

        for (Entity entity : entities) {
            if (entity.getTeam() != team) continue;
            if (entity instanceof Humanoid || entity instanceof Player) {
                Vector2f center = new Vector2f(entity.getPosition().getCenter());
                float dist = center.distanceSquared(target);

                if (result == null || dist < bestDist) {
                    bestDist = dist;
                    result = entity;
                }
            }
        }

        return result;
    }

    public void addFrogPirate(Point startingPoint) throws SlickException {
        FrogPirate frogPirate = new FrogPirate(this, startingPoint, startingPoint, Team.THE_FRENCH);
        addEntity(frogPirate);
    }

    public void addFrogPirate(Point startingPoint, Point guardingPoint) throws SlickException {
        FrogPirate frogPirate = new FrogPirate(this, startingPoint, guardingPoint, Team.THE_FRENCH);
        addEntity(frogPirate);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;

        try {
            game.onWorldStateChanged();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public int getNumberOfBosses() {
        return numberOfBosses;
    }

    public void setNumberOfBosses(int numberOfBosses) {
        this.numberOfBosses = numberOfBosses;
    }

    public boolean hasPirates() {
        for (Entity entity : entities) {
            if (entity.getTeam() == Team.THE_FROG_PIRATES && entity instanceof Humanoid) {
                return true;
            }
        }

        return false;
    }

    public void removeEasterEgg() {
        easterEgg = null;
    }
}

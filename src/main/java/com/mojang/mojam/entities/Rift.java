package com.mojang.mojam.entities;

import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

import java.util.Random;

public class Rift extends Entity {
    public static final float SPAWN_RATE_VARIANCE = 2 * 1000;
    public static final int SPAWN_RATE = 15 * 1000;

    private final long spawnRate;
    private final float minGuardingX;
    private final float maxGuardingX;
    private long lastSpawnTime = 0;
    private boolean closed;

    private final Random random = new Random();

    public Rift(World world, Point startingPoint, float minGuardingX, float maxGuardingX) throws SlickException {
        super(world, new Image("graphics/empty.png"), startingPoint, 1, 1, Team.THE_FROG_PIRATES);
        this.spawnRate = SPAWN_RATE;
        closed = true;
        this.minGuardingX = minGuardingX;
        this.maxGuardingX = maxGuardingX;
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        long now = System.currentTimeMillis();
        if (!closed && ((now - spawnRate) > lastSpawnTime)) {
            world.addFrogPirate(startingPoint, getRandomGuardingPoint());

            double variance = (random.nextGaussian() * SPAWN_RATE_VARIANCE * 2) - SPAWN_RATE_VARIANCE;
            lastSpawnTime = now + (int) variance;
        }
    }

    @Override
    public void render(Camera camera, GameContainer gameContainer) {
        // Don't render.
    }

    private Point getRandomGuardingPoint() {
        if (world.getNumberOfBosses() == 0) {
            return new Point(0, World.FLOOR_LEVEL);
        } else {
            float randomX = minGuardingX + random.nextInt((int) (maxGuardingX - minGuardingX));
            return new Point(randomX, World.FLOOR_LEVEL);
        }
    }

    @Override
    public void decreaseHealth(int amount) throws SlickException {
        // Don't decrease health.
    }

    public void close() {
        closed = true;
    }

    public void open() {
        closed = false;
    }
}

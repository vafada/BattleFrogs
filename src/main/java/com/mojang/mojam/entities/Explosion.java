package com.mojang.mojam.entities;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

import java.util.Random;

public class Explosion extends Entity {
    public static final int TYPE_FROMAGE = 0;
    public static final int TYPE_TADPOLE = 1;
    public static final int TYPE_TADPOLE_SPLATTER = 2;

    public static final int[] ANIMATION_FRAME_RATE = {
            83, // fromage
            83, // tadpole
            83, // splatter
    };
    public static final int[] ANIMATION_FRAME_COUNT = {
            8, // fromage
            8, // tadpole
            7, // splatter
    };
    public static final int[] FRAME_WIDTH = {
            100, // fromage
            125, // tadpole
            100, // splatter
    };
    public static final int[] FRAME_HEIGHT = {
            100, // fromage
            117, // tadpole
            100, // splatter
    };
    public static final String[] FILENAME = {
            "graphics/explosion_fromage.png", // fromage
            "graphics/explosion_tadpole.png", // tadpole
            "graphics/explosion_splatter.png", // splatter
    };

    protected final Animation animation;
    protected final int type;
    protected final int maxLifetime;

    public Explosion(World world, Point startingPoint, int type) throws SlickException {
        super(world, new Image(FILENAME[type]), new Point(startingPoint.getX() - FRAME_WIDTH[type] / 2, startingPoint.getY() - FRAME_HEIGHT[type] / 2), FRAME_WIDTH[type], FRAME_HEIGHT[type], Team.SYSTEM);

        Random random = new Random();
        float variation = 5;
        position.setX(position.getX() + random.nextFloat() * variation * 2 - variation);
        position.setY(position.getY() + random.nextFloat() * variation * 2 - variation);
        this.type = type;
        animation = new Animation(animationSheet, ANIMATION_FRAME_RATE[type]);
        animation.setLooping(false);
        maxLifetime = ANIMATION_FRAME_RATE[type] * ANIMATION_FRAME_COUNT[type];
        facing = random.nextBoolean() ? FACING_LEFT : FACING_RIGHT;
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        animation.update(deltaTime);
        if (gameContainer.getTime() - lifeStart >= maxLifetime) {
            die();
        }
    }

    @Override
    protected Image getFrame() {
        return animation.getCurrentFrame();
    }

    @Override
    public boolean ignoreCollision() {
        return true;
    }
}

package com.mojang.mojam.entities.obstacles;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.screen.LockedDoorScreen;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Door extends Obstacle {

    public static final float MESSAGE_RANGE = 100;
    private final Sound explosionFx;

    public Door(World world, String imagePath, Point startingPoint, int width, int height, Rectangle collisionBox) throws SlickException {
        super(world, new Image(imagePath), startingPoint, width, height, Team.THE_FROG_PIRATES, collisionBox);

        explosionFx = new Sound("sounds/Door_Explosion.wav");

        setFullHealth(1);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        Entity player = world.getNearestEntity(new Vector2f(getPosition().getCenter()), Team.THE_FRENCH);
        float deltaX = player.deltaX(this);
        float deltaY = player.deltaY(getCollisionHitbox());

        if (deltaX <= MESSAGE_RANGE && deltaY == 0) {
            BattleToads game = world.getGame();
            game.setScreen(new LockedDoorScreen(game));
        }
    }

    @Override
    protected void die() throws SlickException {
        super.die();
        SoundManager.getInstance().playSoundEffect(explosionFx);
        onDestroyed();
    }

    protected void onDestroyed() throws SlickException {
    }
}

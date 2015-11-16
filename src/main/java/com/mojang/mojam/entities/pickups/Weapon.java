package com.mojang.mojam.entities.pickups;

import com.mojang.mojam.Camera;
import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.entities.Player;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

public class Weapon extends Pickup {
    private Sound pickupFx;

    public Weapon(World world, Point startingPoint) throws SlickException {
        super(world, new Image("graphics/weapon.png"), startingPoint, 80, 36, Team.THE_FRENCH);

        pickupFx = new Sound("sounds/weapon_pickup_v2.wav");
    }

    @Override
    protected boolean isEligible(Entity entity) {
        return super.isEligible(entity) && entity instanceof Player;
    }

    @Override
    public void render(Camera camera, GameContainer gameContainer) {
        Graphics graphics = gameContainer.getGraphics();
        graphics.pushTransform();
        graphics.translate(0, (float) (Math.sin(gameContainer.getTime() / 400f) * 10) - 25f);
        super.render(camera, gameContainer);
        graphics.popTransform();
    }

    @Override
    protected void applyEffect(Entity entity) {
        Player player = (Player) entity;
        player.setHasWeapon(true);
        world.setState(World.State.WEAPON_PICKED_UP);
        SoundManager.getInstance().playSoundEffect(pickupFx);
    }

    @Override
    protected Shape getPickupHitbox() {
        int padding = 50;
        return new Rectangle(position.getX() - padding, position.getY(), position.getWidth() + padding, position.getHeight());
    }
}

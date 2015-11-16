package com.mojang.mojam.entities.pickups;

import com.mojang.mojam.Team;
import com.mojang.mojam.World;
import com.mojang.mojam.entities.Entity;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Point;

public class Croissant extends Pickup {

    private final Sound pickupSound;

    public Croissant(World world, Point startingPoint) throws SlickException {
        super(world, new Image("graphics/croissant.png"), startingPoint, 52, 48, Team.THE_FRENCH);
        pickupSound = new Sound("sounds/AnnaB_Health_Pickup_v2.wav");
    }

    @Override
    protected boolean isEligible(Entity entity) {
        return super.isEligible(entity) && entity.getHealth() < entity.getHealthMax() - 25;
    }

    @Override
    protected void applyEffect(Entity entity) {
        entity.increaseHealth(25);
        SoundManager.getInstance().playSoundEffect(pickupSound);
    }
}

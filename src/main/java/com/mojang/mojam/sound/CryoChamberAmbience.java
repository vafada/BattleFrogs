package com.mojang.mojam.sound;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class CryoChamberAmbience extends EnvironmentSound {

    public CryoChamberAmbience(Point location, float range) throws SlickException {
        super("sounds/Cryo_Chamber_Ambience-15dB.wav", location, range);
    }
}

package com.mojang.mojam.sound;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class ReactorAmbience extends EnvironmentSound {

    public ReactorAmbience(Point location, float range) throws SlickException {
        super("sounds/Reactor_Loop-15dB.wav", location, range);
    }
}

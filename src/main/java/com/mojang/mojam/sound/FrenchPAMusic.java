package com.mojang.mojam.sound;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class FrenchPAMusic extends EnvironmentSound {

    public FrenchPAMusic(Point location, float range) throws SlickException {
        super("sounds/French_Walz_in_PA.wav", location, range);
    }
}

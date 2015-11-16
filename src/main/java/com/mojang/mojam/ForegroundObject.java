package com.mojang.mojam;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class ForegroundObject {

    private final Image image;
    private final Point position;
    private final float width;
    private final float height;

    public ForegroundObject(Image image, Point position, float width, float height) throws SlickException {
        this.image = image;
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public void render(Camera camera, GameContainer gameContainer) {
        image.draw(position.getX(), position.getY(), width, height);
    }
}

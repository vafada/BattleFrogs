package com.mojang.mojam;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Point;

public class EasterEgg {

    private final SpriteSheet animationSheet;
    private final Animation animation;
    private final Image image;
    private final Point position;
    private final int width;
    private final int height;
    private final World world;

    public EasterEgg(World world, int y, int frameCount) throws SlickException {
        this.world = world;

        width = 108;
        height = 108;

        image = new Image("graphics/splodinkittensheet.png");
        animationSheet = new SpriteSheet(image, width, height);
        animation = new Animation(animationSheet, 0, y, frameCount, y, true, 166, true);
        position = new Point(568, 280);

        animation.start();
        animation.setCurrentFrame(0);
        animation.stopAt(frameCount);
    }

    public void update(int deltaTime) {
        if (animation.getFrame() == animation.getFrameCount() - 1) {
            world.removeEasterEgg();
        }
        animation.update(deltaTime);
    }

    public void render(GameContainer gameContainer, Camera camera) {
        Image image = animation.getCurrentFrame();
        image.draw(position.getX() + camera.getX(), position.getY());
    }
}

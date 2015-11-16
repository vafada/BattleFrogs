package com.mojang.mojam;

import org.newdawn.slick.geom.Vector2f;

public class Camera {
    private final BattleToads game;
    private final Vector2f size;

    public Camera(BattleToads game, Vector2f size) {
        this.game = game;
        this.size = size;
    }

    public float getX() {
        float x = game.getPlayer().getPosition().getCenterX() - (size.getX() / 2);
        Vector2f bounds = game.getWorld().getSize();

        if (x < 0) x = 0;
        if (x > bounds.getX() - size.getX()) x = bounds.getX() - size.getX();

        return x;
    }
}

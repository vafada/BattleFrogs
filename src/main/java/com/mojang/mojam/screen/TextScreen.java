package com.mojang.mojam.screen;

import com.mojang.mojam.BattleToads;
import org.newdawn.slick.*;

public class TextScreen extends Screen {

    private static final int TIME_TO_SHOW = 5 * 1000;
    private long startTime;
    private String text;
    private int textWidth;
    private int textHeight;

    public TextScreen(BattleToads game, String text) {
        super(game);
        this.text = text;
    }

    @Override
    public void onStart() {
        GameContainer gameContainer = game.getGameContainer();
        Graphics graphics = gameContainer.getGraphics();

        startTime = gameContainer.getTime();
        Font font = graphics.getFont();
        textWidth = font.getWidth(text);
        textHeight = font.getHeight(text);
    }

    @Override
    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
        long delta = gameContainer.getTime() - startTime;

        if (delta > TIME_TO_SHOW) {
            game.setScreen(null);
        }
    }

    @Override
    public void render(GameContainer gameContainer) {
        Graphics graphics = gameContainer.getGraphics();
        graphics.setColor(Color.white);
        graphics.drawString(text, (gameContainer.getWidth() / 2f) - (textWidth / 2f), (gameContainer.getHeight() / 3f) - (textHeight / 2f));
    }
}

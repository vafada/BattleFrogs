package com.mojang.mojam.screen;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.*;

public class WinScreen extends Screen {
    private static final int TIME_TO_SHOW = 60 * 1000;
    private long startTime;
    private final Image image;
    private String text = "";
    private int textWidth;
    private int textHeight;

    public WinScreen(BattleToads game) throws SlickException {
        super(game);
        image = new Image("graphics/win.png");
    }

    @Override
    public void onStart() throws SlickException {
        GameContainer gameContainer = game.getGameContainer();
        Graphics graphics = gameContainer.getGraphics();
        long score = gameContainer.getTime() - game.getStartTime();

        text = "Time: " + score + "ms";
        startTime = gameContainer.getTime();
        Font font = graphics.getFont();
        textWidth = font.getWidth(text);
        textHeight = font.getHeight(text);

        game.setPlaying(false);
    }

    @Override
    public void onStop() throws SlickException {
        if (!game.isPlaying()) {
            game.setPlaying(true);
        }
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
        image.draw();
        graphics.setColor(Color.black);
        graphics.drawString(text, 20, 20);
    }
}

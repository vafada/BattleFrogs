package com.mojang.mojam.screen;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.*;

public class DeathScreen extends Screen {
    private static final int TIME_TO_SHOW = 5 * 1000;
    private final Image image;

    private final Sound gameOverSound;
    private long startTime;

    public DeathScreen(BattleToads game) throws SlickException {
        super(game);

        gameOverSound = new Sound("sounds/Space_Ambience_Boooom.wav");
        image = new Image("graphics/gameover.png");
    }

    @Override
    public void onStart() throws SlickException {
        SoundManager.getInstance().playSoundEffect(gameOverSound);
        GameContainer gameContainer = game.getGameContainer();

        startTime = gameContainer.getTime();
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
        image.draw();
    }
}

package com.mojang.mojam.screen;

import com.mojang.mojam.BattleToads;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

public abstract class Screen {
    public final BattleToads game;

    protected Screen(BattleToads game) {
        this.game = game;
    }

    public void onStart() throws SlickException {
    }

    public void onStop() throws SlickException {
    }

    public void update(GameContainer gameContainer, int deltaTime) throws SlickException {
    }

    public abstract void render(GameContainer gameContainer);
}

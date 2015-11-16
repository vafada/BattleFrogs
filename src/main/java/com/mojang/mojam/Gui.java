package com.mojang.mojam;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;

public class Gui {
    private final BattleToads game;
    private final Image healthFull;
    private final Image healthBite;
    private final Image healthEmpty;

    public Gui(BattleToads game, Image healthFull, Image healthBite, Image healthEmpty) {
        this.game = game;
        this.healthFull = healthFull;
        this.healthBite = healthBite;
        this.healthEmpty = healthEmpty;
    }

    public void render(GameContainer gameContainer) {
        renderHealth(gameContainer);
    }

    public void renderHealth(GameContainer gameContainer) {
        float pct = game.getPlayer().getHealth() / (float) game.getPlayer().getHealthMax();

        float width = healthFull.getWidth() * pct;
        float barX0 = 10;
        float barX1 = barX0 + width;
        float barY0 = 10;
        healthEmpty.draw(barX0, barY0);
        healthFull.draw(barX0, barY0, barX0 + width, barY0 + healthFull.getHeight(), 0, 0, width, healthFull.getHeight());

        if (pct < 1 && pct > 0) {
            healthBite.draw(barX1 - 10, barY0 + 1);
        }
    }
}

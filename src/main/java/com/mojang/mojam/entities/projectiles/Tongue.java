package com.mojang.mojam.entities.projectiles;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.Camera;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.ai.actions.enemy.TongueAttack;
import com.mojang.mojam.entities.Humanoid;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Tongue extends Projectile {
    private static final int RANGE = TongueAttack.RANGE + 100;
    private static final int DAMAGE = 15;
    public static final int SPEED = 12;

    public Tongue(Humanoid origin, Attack attack, int width, int height) throws SlickException {
        super(new Image("graphics/empty.png"), origin, attack, width, height, RANGE, DAMAGE);
        horizontalSpeed = facing == FACING_RIGHT ? SPEED : -SPEED;
    }

    @Override
    public void render(Camera camera, GameContainer gameContainer) {
        // Don't render me.
        if (BattleToads.ALLOW_DEBUGGING) gameContainer.getGraphics().setColor(Color.cyan);
        if (BattleToads.ALLOW_DEBUGGING) gameContainer.getGraphics().drawRect(position.getX(), position.getY(), frameSize.getWidth(), frameSize.getHeight());
    }

    @Override
    protected void die() throws SlickException {
        super.die();
        getAttack().setExecuting(false);
    }
}

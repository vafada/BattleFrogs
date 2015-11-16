package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.Team;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.entities.Humanoid;
import com.mojang.mojam.entities.projectiles.Tongue;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class TongueAttack extends Attack {
    private static final int COOLDOWN_MS = 1100;
    public static final int RANGE = 250;

    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;

    private final Sound tongueWhipFx;

    public TongueAttack(Team opposingTeam) throws SlickException {
        super(opposingTeam, RANGE, COOLDOWN_MS);
        tongueWhipFx  = new Sound("sounds/Frog_Tongue_Whip.wav");
    }

    @Override
    public void performAttack(Humanoid entity) {
        try {
            SoundManager.getInstance().playSoundEffect(tongueWhipFx);
            Tongue tongue = new Tongue(entity, this, WIDTH, HEIGHT);
            entity.getWorld().addEntity(tongue);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}

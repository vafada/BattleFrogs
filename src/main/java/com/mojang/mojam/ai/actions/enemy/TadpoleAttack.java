package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.Team;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.entities.Humanoid;
import com.mojang.mojam.entities.projectiles.Missile;
import com.mojang.mojam.entities.projectiles.Tadpole;
import com.mojang.mojam.entities.projectiles.Tongue;
import com.mojang.mojam.sound.SoundManager;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class TadpoleAttack extends Attack {
    public static final int COOLDOWN_MS = 1200;
    public static final int RANGE = Missile.RANGE;

    public TadpoleAttack(Team opposingTeam) throws SlickException {
        super(opposingTeam, RANGE, COOLDOWN_MS);
    }

    @Override
    public void performAttack(Humanoid entity) {
        // Do nothing, handled by magic in boss
    }
}

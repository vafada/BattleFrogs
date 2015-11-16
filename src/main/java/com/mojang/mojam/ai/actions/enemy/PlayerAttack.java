package com.mojang.mojam.ai.actions.enemy;

import com.mojang.mojam.Team;
import com.mojang.mojam.ai.actions.Attack;
import com.mojang.mojam.entities.Humanoid;

public class PlayerAttack extends Attack {
    public PlayerAttack(Team opposingTeam, float range, long cooldownMs) {
        super(opposingTeam, range, cooldownMs);
    }

    @Override
    protected void performAttack(Humanoid entity) {
    }
}

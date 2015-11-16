package com.mojang.mojam.screen;

import com.mojang.mojam.BattleToads;
import com.mojang.mojam.World;

public class LockedDoorScreen extends TextScreen {

    public LockedDoorScreen(BattleToads game) {
        super(game, game.getWorld().getState() == World.State.WEAPON_PICKED_UP ? "This door is locked. Blow it up!" : "This door is locked. You need to find the key.");
    }
}

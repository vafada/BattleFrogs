package com.mojang.mojam.screen;

import com.mojang.mojam.BattleToads;
import org.newdawn.slick.*;

public class InstructionsScreen extends TextScreen {

    public InstructionsScreen(BattleToads game) {
        super(game, "Use WASD/Arrow keys to move and jump.");
    }
}

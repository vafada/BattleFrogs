package com.mojang.mojam;

import com.mojang.mojam.screen.CheatScreen;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.List;

public class KeySequenceReader {

    private final BattleToads game;
    private final List<Sequence> sequences = new ArrayList<Sequence>();
    private String currentSequence = "";

    public KeySequenceReader(BattleToads game) {
        this.game = game;

        sequences.add(new Sequence("krislovesjulia", new Effect() {

            @Override
            protected void invokeEffect(BattleToads game) {
                game.getPlayer().setHasWeapon(true);
            }
        }));
        sequences.add(new Sequence("babyolenka", new Effect() {

            @Override
            protected void invokeEffect(BattleToads game) {
                game.getPlayer().increaseHealth(500);
            }
        }));
        sequences.add(new Sequence("kungfufighting", new Effect() {
            @Override
            protected void invokeEffect(BattleToads game) {
                game.getPlayer().setDamageModifier(5);
            }
        }));
        sequences.add(new Sequence("ohcamaro", new Effect() {
            @Override
            protected void invokeEffect(BattleToads game) {
                game.getPlayer().setInvulnerable(true);
            }
        }));
    }

    public void appendChar(char c) {
        currentSequence += c;
        Sequence matchingSequence = getFullyMatchingSequence();
        if (matchingSequence != null) {
            matchingSequence.effect.invokeEffect(game);
            game.scorePenalty(1000 * 60);
            try {
                game.setScreen(new CheatScreen(game));
            } catch (SlickException e) {
                e.printStackTrace();
            }
            currentSequence = "";
        } else if (!hasPartialMatchingSequence()) {
            currentSequence = "";
        }
    }

    private Sequence getFullyMatchingSequence() {
        for (Sequence sequence : sequences) {
            if (sequence.sequence.equalsIgnoreCase(currentSequence)) {
                return sequence;
            }
        }
        return null;
    }

    private boolean hasPartialMatchingSequence() {
        for (Sequence sequence : sequences) {
            if (sequence.sequence.startsWith(currentSequence)) {
                return true;
            }
        }
        return false;
    }

    private static class Sequence {

        private String sequence;
        private Effect effect;

        private Sequence(String sequence, Effect effect) {
            this.sequence = sequence;
            this.effect = effect;
        }
    }

    private abstract static class Effect {

        protected abstract void invokeEffect(BattleToads game);
    }
}

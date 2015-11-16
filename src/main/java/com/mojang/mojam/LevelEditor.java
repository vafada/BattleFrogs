package com.mojang.mojam;

import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;

public class LevelEditor implements MouseListener {
    private final BattleToads game;
    private int clickX;
    private int clickY;

    public LevelEditor(BattleToads game) {
        this.game = game;
    }

    @Override
    public void mouseWheelMoved(int i) {
    }

    @Override
    public void mouseClicked(int i, int i2, int i3, int i4) {
    }

    @Override
    public void mousePressed(int button, int mouseX, int mouseY) {
        int x = (int) (mouseX + game.getCamera().getX());
        int y = mouseY;

        this.clickX = x;
        this.clickY = y;
    }

    @Override
    public void mouseReleased(int button, int mouseX, int mouseY) {
        int x = (int) (mouseX + game.getCamera().getX());
        int y = mouseY;
        int x0 = Math.min(x, clickX);
        int x1 = Math.max(x, clickX);
        int y0 = Math.min(y, clickY);
        int y1 = Math.max(y, clickY);
        int width = x1 - x0;
        int height = y1 - y0;

        System.out.println(x0 + " " + y0 + " " + width + " " + height);
    }

    @Override
    public void mouseMoved(int i, int i2, int i3, int i4) {
    }

    @Override
    public void mouseDragged(int i, int i2, int i3, int i4) {
    }

    @Override
    public void setInput(Input input) {
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public void inputStarted() {
    }
}

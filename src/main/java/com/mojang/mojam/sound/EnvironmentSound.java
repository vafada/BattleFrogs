package com.mojang.mojam.sound;

import com.mojang.mojam.World;
import com.mojang.mojam.entities.Player;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Point;

public class EnvironmentSound {

    private static final int FADE_OUT_DURATION = 1000 * 3;
    private static final int FADE_IN_DURATION = 1000 * 3;

    private Music sound;
    private Point location;
    private float range;
    private boolean playing;
    private boolean wasMuted = false;
    private SoundManager soundManager;

    public EnvironmentSound(String soundPath, Point location, float range) throws SlickException {
        this.sound = new Music(soundPath);
        this.location = location;
        this.range = range;
        soundManager = SoundManager.getInstance();
    }

    public void update(Player player, World.State state) {
        if (playing && soundManager.isMuted()) {
            stopMutedLoop();
        }

        if (isWithinRange(player) && World.State.GAME_OVER != state) {
            if (!playing) {
                if (wasMuted && !soundManager.isMuted()) {
                    resumeMutedLoop();
                } else {
                    if (!playing) {
                        playLoop();
                    }
                }
            }
        } else if (playing) {
            stopLoop();
        }
    }

    private void stopMutedLoop() {
        soundManager.pauseMusic(sound);
        wasMuted = true;
    }

    private void stopLoop() {
        soundManager.fadeOutMusic(sound, FADE_OUT_DURATION);
        playing = false;
    }

    private void playLoop() {
        soundManager.fadeInMusic(sound, FADE_IN_DURATION);
        playing = true;
    }

    private void resumeMutedLoop() {
        soundManager.resumeMusic(sound);
        wasMuted = false;
    }

    private boolean isWithinRange(Player player) {
        float deltaX = player.deltaX(location);
        return deltaX <= range;
    }
}

package com.mojang.mojam.sound;

import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

import java.util.Random;

public final class SoundManager {

    private static SoundManager instance;

    private boolean muted = false;

    private final Random random = new Random(System.currentTimeMillis());

    private SoundManager() {
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playSoundEffect(Sound soundEffect) {
        if (!muted) {
            soundEffect.play();
        }
    }

    public void playRandomSoundEffect(Sound[] soundEffects) {
        int r = random.nextInt(soundEffects.length);
        SoundManager.getInstance().playSoundEffect(soundEffects[r]);
    }

    public void playLoop(Sound sound) {
        if (!muted) {
            sound.loop();
        }
    }

    public void stop(Sound sound) {
        sound.stop();
    }

    public void mute() {
        muted = true;
    }

    public void unmute() {
        muted = false;
    }

    public void toggleMute() {
        if (muted) {
            unmute();
        } else {
            mute();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void pauseMusic(Music music) {
        music.pause();
    }

    public void fadeOutMusic(Music music, int duration) {
        music.fade(duration, 0, true);
    }

    public void fadeInMusic(Music music, int duration) {
        if (!muted) {
            music.loop(1.0f, 0);
            music.fade(duration, 1.0f, false);
        }
    }

    public void resumeMusic(Music music) {
        if (!muted) {
            music.resume();
        }
    }
}

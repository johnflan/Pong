package com.johnflan;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.File;

public class AudioManager {
    private static final String SOUNDS_LOCATION = "resources/sounds/";
    private static final String WALL = SOUNDS_LOCATION + "wall.wav";
    private static final String PADDLE = SOUNDS_LOCATION + "paddle.wav";
    private static final String SCORE = SOUNDS_LOCATION + "score.wav";

    private boolean audioOn = true;

    public void toggleAudio() {
        audioOn = !audioOn;
    }

    public void playWallHit() {
        playFile(WALL);
    }

    public void playPaddleHit() {
        playFile(PADDLE);
    }

    public void playScore() {
        playFile(SCORE);
    }

    private void playFile(String filename) {

        if (!audioOn) {
            return;
        }

        try {
            final Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));

            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP)
                        clip.close();
                }
            });

            clip.start();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }
}

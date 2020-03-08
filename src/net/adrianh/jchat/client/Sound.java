package net.adrianh.jchat.client;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;


// sound class, creates a new sound with a given name and plays it. 
// implement given name, now every sound is default test.wav. Startup sound and message sound?
// what happens if user closes application? does the sound stop?
public class Sound {
    private URL url;
    private Clip clip;
    public Sound(String requestedSound) {
        if (requestedSound.equals("message")) {
            url = this.getClass().getResource("/newMessage.wav");
        }
        if (url != null) {
            try {
                // Open an audio input stream.
                // Get a sound clip resource.
                // Open audio clip and load samples from the audio input stream.
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioInput);


            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void play() {
        clip.setFramePosition(0);
        clip.start();
    }
}
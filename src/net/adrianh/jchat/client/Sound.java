package net.adrianh.jchat.client;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;


// sound class, creates a new sound with a given name and plays it. 
// implement given name, now every sound is default test.wav. Startup sound and message sound?
// what happens if user closes application? does the sound stop?
public class Sound {
    public Sound() {
        try {
            // Open an audio input stream.
            URL url = this.getClass().getClassLoader().getResource("test.wav");
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioInput);
            clip.start();
         } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (LineUnavailableException e) {
            e.printStackTrace();
         }
    }
}
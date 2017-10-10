package net.undergroundim.client;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Sound is used to create a sound object out of a .wav
 * or .ogg file.
 * 
 * Sound has basic method to play and stop the file.
 * When playing the sound you may specify the volume.
 * 
 * @author Troy
 * @version 1.00
 *
 */
public class Sound {
	private Clip clip;
	private URL url;
	private AudioInputStream audioInputStream;
	private FloatControl floatControl;
	AudioFormat format;
	DataLine.Info info;
	
	/**
	 * 
	 * @param soundFileName
	 */
	public Sound(String soundFileName){
		try{
			// Use URL (instead of File) to read from disk and JAR.
			url = this.getClass().getClassLoader().getResource(soundFileName);
	        // Set up an audio input stream piped from the sound file.
	        audioInputStream = AudioSystem.getAudioInputStream(url);
	        // Get a clip resource.
	        format = audioInputStream.getFormat();
	        info = new DataLine.Info(Clip.class, format);
	        clip = (Clip) AudioSystem.getLine(info);
	        //clip = AudioSystem.getClip();
	        // Open audio clip and load samples from the audio input stream.
	        clip.open(audioInputStream);
		}catch(UnsupportedAudioFileException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Play the sound.
	 */
	public void play(float volume) {
		if(clip.isRunning())
			clip.stop();   // Stop the player if it is still running
		clip.setFramePosition(0); // rewind to the beginning
		
		//Set volume
		if(System.getProperty("java.vendor").contains("Oracle Corporation")){
			floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			floatControl.setValue(volume);
		}
		
		clip.start();     // Start playing
	}
	
	/**
	 * Stop the sound.
	 */
	public void stop(){
		if(clip.isRunning())
			clip.stop();   // Stop the player if it is still running
		clip.setFramePosition(0); // rewind to the beginning
	}
	
	/**
	 * Check if the sound is playing.
	 * @return
	 */
	public boolean isPlaying(){
		return clip.isRunning();
	}

}

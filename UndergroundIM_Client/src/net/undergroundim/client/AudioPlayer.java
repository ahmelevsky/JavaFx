package net.undergroundim.client;


/**
 * 
 * @author Troy
 *
 */
public class AudioPlayer {
	public static Sound MESSAGE;
	public static Sound ONLINE;
	public static Sound NUDGE;
	public static Sound ALERT;
	public static Sound FILE;
	public boolean complete = true;
	
	public AudioPlayer(){
		MESSAGE = new Sound("resources/sounds/type.wav");
		ONLINE = new Sound("resources/sounds/online.wav");
		NUDGE = new Sound("resources/sounds/nudge.wav");
		ALERT = new Sound("resources/sounds/newalert.wav");
		FILE = new Sound("resources/sounds/vimdone.wav");
	}

}
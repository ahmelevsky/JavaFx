package net.undergroundim.server.networking;

import net.undergroundim.server.Client;
import net.undergroundim.server.Constants;

/**
 * 
 * @author Troy
 *
 */
public class Packets {
	
	/**
	 * This will generate the friend's list
	 * for the selected user id.
	 * 
	 * @param user_id
	 * @return String
	 */
	public static String friendList(int user_id){
		String packet = PacketHeaders.FRIEND_LIST.getHeader() + "1~";
		
		for(Client c : Constants.getJdbc().getFriends(user_id)){
			packet += c.getUser_id() + ":" + 
					c.getUsername() + ":" + 
					c.getScreen_name() + ":" +
					c.getStatus() + ":" +
					c.isOnline() + ",";
		}

		return packet;
	}
	
	/**
	 * Get all friend Requests.
	 * 
	 * @param user_id
	 * @return String
	 */
	public static String friendRequests(int user_id){
		String packet = PacketHeaders.FRIEND_ADD.getHeader() + "1~";
		
		packet += Constants.getJdbc().getFriendRequests(user_id);

		if(!packet.equals("9~1~"))
			return packet;
		else
			return null;
	}
	
	/**
	 * View a user's profile.
	 * 
	 * @param user_id
	 * @return String
	 */
	public static String viewProfile(int user_id, int friend_id){
		String packet = PacketHeaders.VIEW_PROFILE.getHeader() + ":";
		
		packet += Constants.getJdbc().viewProfile(user_id, friend_id);
		
		return packet;
	}

}
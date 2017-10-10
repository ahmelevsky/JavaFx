package net.undergroundim.server;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.crypto.spec.SecretKeySpec;

import net.undergroundim.server.encryption.AESEncoder;
import net.undergroundim.server.encryption.Encoder;
import net.undergroundim.server.networking.Broadcaster;
import net.undergroundim.server.networking.PacketHeaders;
import net.undergroundim.server.networking.Packets;


/**
 * 
 * The client thread is in charge of handling
 * all communications between server-client.
 * 
 * While the client is connected the key will
 * be randomly changed after a random interval.
 * 
 * @author Troy
 *
 */
public class ClientThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private AESEncoder encoder;
    private SecretKeySpec key;
    
    public int clientID;
    public Client client;
    
    private String fromClient;
    private String[] header;
    private String[] packet;
    private String[] clients;
    
    public boolean loggedin,connected, transfer;
    public Timer keyChangeTimer;
    
    private Timer nudgeTimer = new Timer(10000);

    /**
     * Start a new thread
     * 
     * @param socket
     * @param clientNumber
     */
    public ClientThread(Socket socket, int clientID) {
        this.socket = socket;
        try {
        	this.socket.setTcpNoDelay(true);
        	this.socket.setKeepAlive(true);
        	this.socket.setSendBufferSize(30720);
        	this.socket.setReceiveBufferSize(30720);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        this.clientID = clientID;
        this.key = Constants.generateSecretKey();
        this.encoder = new AESEncoder(key);
        log("New client " + clientID + ": " + socket, Color.GREEN);
        this.loggedin = false;
        keyChangeTimer = new Timer(Constants.getRandom().random(60000,300000));
        this.start();
    }

    /**
     * Manages the thread while its running.
     */
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while((fromClient = in.readLine()) != null){
            	Constants.getTimeout().reset();
            	
				if(connected)
					fromClient = encoder.decrypt(fromClient.trim());
				else
					fromClient = Encoder.decode(fromClient);

				if(fromClient != null && fromClient.contains("~") && fromClient.endsWith(";")){
					//Remove end character.
					fromClient = fromClient.replace(";", ""); 
					
					//Get header
					header = fromClient.split("~");
					
					//Get packet
					if(header[1].contains(":"))
						packet = header[1].split(":");
	                
	                /**
	                 * Switch through packet headers.
	                 * 
	                 * Client->Server
	                 * 
	                 * We must response with the same header
	                 * packet so the client know's how to process
	                 * the response.
	                 */
	                switch(Integer.parseInt(header[0])){
	                case 0: //Login
	                	/**
	                	 * Send the login packet with the client's id and key.
	                	 */
	                	if(packet[1].equals(Constants.getPassword())){
		                		if(Double.parseDouble(packet[2]) >= Constants.getMinVersionAllowed()){
		                			if(Constants.getClient(packet[3]) != null) //Close other session
		                				Constants.getClient(packet[3]).sendPacket(PacketHeaders.SESSION_CLOSED.getHeader() + ":");
		                			
		                			client = Constants.getJdbc().getUser(clientID, packet[3], packet[4]);
		                			
		                			if(client != null){
		                			    SecretKeySpec keyChange = Constants.generateSecretKey();
		                			    
					                	sendPacket(PacketHeaders.LOGIN.getHeader() + clientID + ":" + 
					                			Constants.bytesToString(keyChange.getEncoded()) + ":" +
					                			client.getUser_id() + ":" +
					                			client.getScreen_name() + ":" +
					                			client.getEmail() + ":" +
					                			client.getAge() + ":" +
					                			client.getSex() + ":" +
					                			client.getLocation() + ":" +
					                			client.getAbout() + ":" +
					                			client.getMade_date());
					                	
		                				this.key = keyChange;
		                			    this.encoder = new AESEncoder(keyChange);
					                	this.loggedin = true;
					                	
					                    /**
					                     * Notify all client's and new user has joined.
					                     */
					                	Broadcaster.broadcastFriends(PacketHeaders.FRIEND_LIST.getHeader() + "1~" +
					                			client.getUser_id() + ":" + 
					        					client.getUsername() + ":" + 
					        					client.getScreen_name() + ":" +
					        					client.getStatus() + ":" +
					        					client.isOnline() + ",",
					        					client.getUser_id());
		                			}else{
		                				sendPacket(PacketHeaders.LOGIN_FAILED.getHeader() + ":");
		    	                		socket.close();
		                			}
	                		}else{
	                			sendPacket(PacketHeaders.VERSION_FAIL.getHeader() + Constants.getMinVersionAllowed() + ":");
		                		socket.close();
	                		}
	                	}else{
	                		sendPacket(PacketHeaders.LOGIN_FAILED.getHeader() + ":");
	                		socket.close();
	                	}
	                	break;
	                case 1://Login Failed..
	                	//Do nothing, should never get this packet?
	                	break;
	                case 2: //Connect
	                	sendRawPacket(Constants.bytesToString(this.key.getEncoded()));
	                	connected = true;
	                	break;
	                case 3: //Packet Error
	                	break;
	                case 4: //Packet New Key
	                	break;
	                case 5: //Version Fail
	                	break;
	                case 6: //Register User
	                	if(packet[1].equals(Constants.getPassword())){
	                		if(Double.parseDouble(packet[2]) >= Constants.getMinVersionAllowed()){
	                			if(Constants.getJdbc().registerUser(packet[3],packet[4])){
	                				sendPacket(PacketHeaders.REGISTER_USER.getHeader() + ":");
	                			}else{
	                				sendPacket(PacketHeaders.REGISTER_FAILED.getHeader() + ":" + "This user already exists.");
	                			}
	                			socket.close();
	                		}else{
	                			sendPacket(PacketHeaders.VERSION_FAIL.getHeader() + Constants.getMinVersionAllowed() + ":");
		                		socket.close();
	                		}
	                	}else{
	                		sendPacket(PacketHeaders.LOGIN_FAILED.getHeader() + ":");
	                		socket.close();
	                	}
	                	break;
	                case 7: //Register Failed
	                	break;
	                case 8: //Friend List
	                	sendPacket(Packets.friendList(client.getUser_id()));
	                	break;
	                case 9: //Friend Add
	                	if(Constants.getJdbc().addFriend(client.getUser_id(), packet[1])){
		                	if(Constants.isClientOnline(packet[1])){
		                		Constants.getClient(packet[1]).sendPacket(PacketHeaders.FRIEND_ADD.getHeader() + "1~" +
		                				client.getUsername() + ":" +
		                				packet[1]);
		                	}
	                	}else{
	                		sendPacket(PacketHeaders.FRIEND_ADD.getHeader() + ":" +
	                				packet[1]);
	                	}
	                	break;
	                case 10: //Friend Delete
	                	if(Constants.getJdbc().deleteFriend(Integer.parseInt(packet[1]), Integer.parseInt(packet[2]))){
	                		sendPacket(PacketHeaders.FRIEND_DELETE.getHeader() + ":" + Integer.parseInt(packet[2]));
	                		if(Constants.isClientOnline(Integer.parseInt(packet[2]))){
	                			Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FRIEND_DELETE.getHeader() + ":" + Integer.parseInt(packet[1]));
	                		}
	                	}
	                	break;
	                case 11: //Friend Request
	                	String p = Packets.friendRequests(client.getUser_id());
	                	
	                	if(p != null)
	                		sendPacket(p);   
	                	break;
	                case 12: //Friend Disconnect
	                	break;
	                case 13: //Request Response
	                	if(Boolean.parseBoolean(packet[1])){
	                		Constants.getJdbc().acceptFriend(packet[2], client.getUser_id());
	                		sendPacket(Packets.friendList(client.getUser_id()));
	                		if(Constants.isClientOnline(packet[2])){
	                			Constants.getClient(packet[2]).sendPacket(Packets.friendList(Constants.getClient(packet[2]).client.getUser_id()));
	                		}
	                	}else{
	                		Constants.getJdbc().declineFriend(client.getUser_id(), packet[3]);
	                	}
	                	break;
	                case 14: //Personal Message
	                	if(packet.length > 1){
	                		if(Boolean.valueOf(packet[7])){
	                			String[] clients = packet[3].split(",");
	                			
	                			for(String s : clients){
	                				if(s != null)
		                				Constants.getClient(Integer.parseInt(s)).sendPacket(PacketHeaders.PERSONAL_MESSAGE.getHeader() + ":" +
				                				packet[1] + ":" +
		                						packet[2] + ":" +
				                				packet[4] + ":" +
				                				packet[5] + ":" +
				                				packet[6] + ":" +
				                				packet[7]);
	                			}
	                		}else{
		                		Constants.getClient(Integer.parseInt(packet[3])).sendPacket(PacketHeaders.PERSONAL_MESSAGE.getHeader() + ":" +
		                				packet[1] + ":" +
		                				packet[2] + ":" + 
		                				packet[4] + ":" +
		                				packet[5] + ":" +
		                				packet[6] + ":" +
		                				packet[7]);
	                		}
	                	}
	                	break;
	                case 15: //View Profile
	                	sendPacket(Packets.viewProfile(client.getUser_id(), Integer.parseInt(packet[1])));
	                	break;
	                case 16: //Update Profile
	                	client.setScreen_name(packet[1]);
	                	client.setEmail(packet[2]);
	                	client.setAge(Byte.valueOf(packet[3]));
	                	client.setSex(Byte.valueOf(packet[4]));
	                	client.setLocation(packet[5]);

	                	if(packet.length > 6)
	                		client.setAbout(packet[6]);
	                	
	                	Constants.getJdbc().updateProfile(client);
	                	break;
	                case 17: //Update Password
	                	if(!Constants.getJdbc().updatePassword(client.getUser_id(), packet[1], packet[2]))
	                		sendPacket(PacketHeaders.UPDATE_PASSWORD.getHeader() + ":");
	                	break;
	                case 18: //Update Status
	                	client.setStatus(Integer.parseInt(packet[1]));
	                	Broadcaster.broadcastFriends(PacketHeaders.FRIEND_LIST.getHeader() + "1~" +
	                			client.getUser_id() + ":" + 
	        					client.getUsername() + ":" + 
	        					client.getScreen_name() + ":" +
	        					client.getStatus() + ":" +
	        					client.isOnline() + ",",
	        					client.getUser_id());
	                	break;
	                case 19: //File Transfer
	                	if(!Constants.isFileTransferEnabled()){
	                		sendPacket(PacketHeaders.SERVER_PERMISSIONS.getHeader() + ":" + 
	                				"File Transfer has been disabled on this server.");
	                	}else{
		                	if(packet.length > 1){
		                		Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FILE_TRANSFER.getHeader() + ":" +
		                				packet[1] + ":" +
		                				packet[2] + ":" +
		                				packet[3]);
		                		this.transfer = true;
		                	}
	                	}
	                	break;
	                case 20: //File Transfer Response
	                	this.transfer = Boolean.parseBoolean(packet[1]);

	                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FILE_TRANSFER_RESPONSE.getHeader() + ":" +
	                			packet[2] + ":" +
	                			packet[3] + ":" +
	                			transfer);
	                	break;
	                case 21: //File Start
	                	if(transfer)
	                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FILE_START.getHeader() + ":" +
	                			packet[1] + ":" +
	                			packet[2] + ":" +
	                			packet[3] + ":" +
	                			packet[4]);
	                	break;
	                case 22: //File Send
	                	if(transfer)
	                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FILE_SEND.getHeader() + ":" +
	                			packet[1] + ":" +
	                			packet[2] + ":" +
	                			packet[3]);
	                	break;
	                case 23: //File End
	                	if(transfer)
	                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FILE_END.getHeader() + ":" +
	                			packet[1] + ":" +
	                			packet[2]);
	                	break;
	                case 24: //File Cancel
	                	transfer = false;
	                	
	                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.FILE_CANCEL.getHeader() + ":" +
	                			packet[1] + ":" +
	                			packet[2]);
	                	break;
	                case 25: //Server permissions
	                	break;
	                case 26: //Nudge
	                	if(nudgeTimer.isUp()){
		                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.NUDGE.getHeader() + ":" +
		                			packet[1] + ":" +
		                			packet[2]);
		                	
		                	nudgeTimer.reset();
	                	}
	                	break;
	                case 27: //User typing
	                	Constants.getClient(Integer.parseInt(packet[2])).sendPacket(PacketHeaders.USER_WRITING.getHeader() + ":" +
	                			packet[1] + ":" +
	                			packet[2] + ":" +
	                			packet[3]);
	                	break;
	                case 28: //Sessions error
	                	break;
	                case 29: //Group Add
	                	clients = header[3].split(",");
            			
            			for(String s : clients){
            				if(s != null)
            					Constants.getClient(Integer.parseInt(s)).sendPacket(PacketHeaders.GROUP_ADD.getHeader() + ":" +
		                				packet[1] + ":" + 
            							packet[2] + ":" + 
		                				packet[3] + ":" + 
            							packet[4] + "~" +
		                				header[2]);
            			}
            			
            			clients = null;
	                	break;
	                case 30:// Group remove
	                	clients = packet[3].split(",");
            			
            			for(String s : clients){
            				if(s != null)
            					Constants.getClient(Integer.parseInt(s)).sendPacket(PacketHeaders.GROUP_REMOVE.getHeader() + ":" +
		                				packet[1] + ":" + 
            							packet[2]);
            			}
            				
            			clients = null;
	                	break;
	                default: //Default should NEVER be reached.
	                	if(!transfer){
		                	sendPacket(PacketHeaders.PACKET_ERROR.getHeader() + ":" + fromClient);
		            		socket.close();
	                	}
	                	break;
	                }
	                
	                /**
	                 * Randomly change the Key at times.
	                 * 
	                 * This is just for more security.
	                 * 
	                 * You must send the original key plus the
	                 * new key to authenticate.
	                 */
	                if(keyChangeTimer.isUp()){
	                	SecretKeySpec keyChange = Constants.generateSecretKey();
	                	sendPacket(PacketHeaders.PACKET_NEW_KEY.getHeader() + ":" + 
	                			Constants.bytesToString(this.key.getEncoded()) + ":" +
	                			Constants.bytesToString(keyChange.getEncoded()));
	                	this.key = keyChange;
	                	this.encoder = new AESEncoder(key);
	                	keyChangeTimer.setTimerToEndIn(Constants.getRandom().random(60000,300000));
	                	keyChangeTimer.reset();
	                }
                }else{
                	//Null packet.
                }
            }
        }catch(IOException e){
            //Do nothing.
        }catch(Exception e2){
        	e2.printStackTrace();
        	sendPacket(PacketHeaders.PACKET_ERROR.getHeader() + ":" + fromClient);
    		try {
				socket.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
        }finally{
            try {
                socket.close();
            } catch (IOException e){
                log("Couldn't close a socket, what's going on?",null);
            }

            /**
             * Tell client's to remove player.
             */
            if(client != null)
            	Broadcaster.broadcastFriends(PacketHeaders.FRIEND_DISCONNECT.getHeader() + ":" + client.getUser_id(), client.getUser_id());
            
            /**
             * Remove player.
             */
            log("Connection with client # " + clientID + " closed.", Color.RED);
            
            /**
             * Finally, kill of this thread and remove it.
             */
            Constants.removeClient(this);
        }
    }

    
    /**
     * Send a raw packet to the client.
     */
    public void sendRawPacket(String packet){
    	if(packet != null){
	    	out.println(Encoder.encode(packet + ";"));
	    	out.flush();
    	}
    }
    
    /**
     * Easier way to encode.
     */
    public void sendPacket(String packet){
    	if(packet != null){
	    	out.println(this.encoder.encrypt(packet + ";"));
	    	out.flush();
    	}
    }
    
    /**
     * Log a message to the server with a coloured line.
     * 
     * @param message
     * @param color
     */
    private void log(String message, Color color) {
        Server.log(message, color);
    }
    
}
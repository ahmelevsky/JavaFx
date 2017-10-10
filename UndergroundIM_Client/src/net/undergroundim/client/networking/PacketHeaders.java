package net.undergroundim.client.networking;

/**
 * 
 * @author Troy
 * 
 *
 */
public enum PacketHeaders {
	LOGIN("0~"),
	LOGIN_FAILED("1~"),
	CONNECT("2~"),
	PACKET_ERROR("3~"),
	PACKET_NEW_KEY("4~"),
	VERSION_FAIL("5~"),
	REGISTER_USER("6~"),
	REGISTER_FAILED("7~"),
	FRIEND_LIST("8~"),
	FRIEND_ADD("9~"),
	FRIEND_DELETE("10~"),
	FRIEND_REQUESTS("11~"),
	FRIEND_DISCONNECT("12~"),
	REQUEST_RESPONSE("13~"),
	PERSONAL_MESSAGE("14~"),
	VIEW_PROFILE("15~"),
	UPDATE_PROFILE("16~"),
	UPDATE_PASSWORD("17~"),
	UPDATE_STATUS("18~"),
	FILE_TRANSFER("19~"),
	FILE_TRANSFER_RESPONSE("20~"),
	FILE_START("21~"),
	FILE_SEND("22~"),
	FILE_END("23~"),
	FILE_CANCEL("24~"),
	SERVER_PERMISSIONS("25~"),
	NUDGE("26~"),
	USER_WRITING("27~"),
	SESSION_CLOSED("28~"),
	GROUP_ADD("29~"),
	GROUP_REMOVE("30~");
	
	private final String header;
	
	private PacketHeaders(String header){
		this.header = header;;
	}

	public String getHeader() {
		return header;
	}

}
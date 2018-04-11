package de.BentiGorlich.BatrikaClient;

public enum MessageStatus {
	none(0),
	received_by_server(1),
	received_by_user(2),
	seen_by_client(3)
	;
	int i;
	MessageStatus(int i){
		this.i = i;
	}
	
	public int toInt() {
		return i;
	}
	public static MessageStatus fromInt(int i ) {
		switch(i) {
		case 0: return none;
		case 1: return received_by_server;
		case 2: return received_by_user;
		case 3: return seen_by_client;
		default: return none;
		}
	}
}

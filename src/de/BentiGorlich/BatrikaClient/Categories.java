package de.BentiGorlich.BatrikaClient;

public enum Categories {
	user(0),
	room(1),
	group(2),
	server(3)
	;
	int i;
	Categories(int i) {
		this.i = i;
	}
	public int toInt() {
		return i;
	}
	public static Categories getFromInt(int i) {
		switch(i) {
		case 0: return user;
		case 1: return room;
		case 2: return group;
		case 3: return server;
		default: return user;
		}
	}
}

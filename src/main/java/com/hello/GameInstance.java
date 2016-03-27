package com.hello;

import sx.blah.discord.handle.obj.IUser;

public class GameInstance {
	private IUser id1;
	private IUser id2;
	private OmokGame og;
	
	public GameInstance() {
		
	}
	
	public boolean isReady() {
		return !(id1 == null || id2 == null);
	}
	
	public IUser getUser1() {
		return id1;
	}
	
	public IUser getUser2() {
		return id2;
	}
	
	/* more practical, simpler than setters */
	public void addUser(IUser u) {
		if (id1 == null) {
			id1 = u;
		} else if (id2 == null) {
			id2 = u;
			createGame();
		}
	}
	
	/*public void setUser1(IUser u) {
		id1 = u;
	}

	public void setUser2(IUser u) {
		id2 = u;
	}*/
	
	/* Precondition: isReady() == true */
	private void createGame() {
		og = new OmokGame(new Player(id1.getID(), "X"), new Player(id2.getID(), "O"));
	}
	
	public OmokGame getGame() {
		return og;
	}
	
	public void resetGame() {
		//og.reset();
		og = null;
		id1 = null;
		id2 = null;
	}
}

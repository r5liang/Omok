package com.hello;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.DiscordException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.IListener;
import sx.blah.discord.handle.EventDispatcher;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.HTTP429Exception;
import sx.blah.discord.api.MissingPermissionsException;
import sx.blah.discord.handle.obj.IUser;

public class OmokDriver implements IListener<MessageReceivedEvent> {
	
	public static final String OMOK_INIT = "omok";
	public static final String OMOK_START = "start";
	public static final String OMOK_RESIGN = "resign";
	public static final String HELP = "!help";
	public static final String RULES = "!rules";
	
	public static OmokDriver INSTANCE; //Singleton instance of the bot.
	public IDiscordClient client; //The instance of the discord client.
	private String id1;
	private String id2;
	private OmokGame og;

	public static void main(String[] args) {
		if (args.length < 2) //Needs an email and password provided
			throw new IllegalArgumentException("This bot needs at least 2 arguments!");
		
		INSTANCE = login(args[0], args[1]); //Creates the bot instance and logs it in.
		
	}
	
	public OmokDriver(IDiscordClient client) {
		this.client = client; //Sets the client instance to the one provided
		EventDispatcher dispatcher = client.getDispatcher(); //Gets the client's event dispatcher
		dispatcher.registerListener(this); //Registers the event listener
		//og = new OmokGame();
		id1 = null;
		id2 = null;
	}
	
	public static OmokDriver login(String email, String password) {
		OmokDriver bot = null; //Initializing the bot variable
		
		ClientBuilder builder = new ClientBuilder(); //Creates a new client builder instance
		builder.withLogin(email, password); //Sets the email and password for the client
		try {
			IDiscordClient client = builder.login(); //Builds the IDiscordClient instance and logs it in
			bot = new OmokDriver(client); //Creating the bot instance
		} catch (DiscordException e) { //Error occurred logging in
			System.err.println("Error occurred while logging in!");
			e.printStackTrace();
		}
		
		return bot;
	}
	
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage(); //Gets the message from the event object NOTE: This is not the content of the message, but the object itself
		IChannel channel = message.getChannel(); //Gets the channel in which this message was sent.
		IUser sender = message.getAuthor();
		//System.out.println("hi");
		
		if (message.getContent().equals(HELP)) {
			sendMessage("I am not a bot. I am a host for the Omok board game on Discord. \n\nType `" + OMOK_INIT + "` to start a game.\n" + 
						"During the game, when it's your turn, type the letter of the column you want to place your piece " +
						"followed by the row number, separated with a space. For example, `C 7`. \n" + 
						"Type `" + OMOK_RESIGN + "` to forfeit the game. \n\n" + 
						"If you are unfamiliar with Omok, type `" + RULES + "` to learn.\n" + 
						"I'm created and worked on by xxdeathx, please contact him for questions and complaints.", channel);
		} else if (message.getContent().equals(RULES)) {
			sendMessage("Omok is a simple game played on a 15x15 board. Two players take turns placing pieces " + 
						"on the board. The goal is to line up 5 in a row of one's own pieces, and doing so wins " +
						"the game. For more information, see https://en.wikipedia.org/wiki/Gomoku", channel);
		}
		
		if (og == null || id1 == null || id2 == null) {
			if (message.getContent().equals(OMOK_INIT)) {
				if (id1 == null) {
					id1 = sender.getID();
					sendMessage("Forming Omok game. Type `" + OMOK_INIT + "` to become player 2.", channel);
				} else {
					id2 = sender.getID();
					og = new OmokGame(new Player(id1, "X"), new Player(id2, "O"));
					sendMessage("Got both players. Type `" + OMOK_START + "` to begin.", channel);
				}	
			}
		} else if (og.getState() == OmokGame.INIT || og.getState() == OmokGame.PLAYER1_WIN || og.getState() == OmokGame.PLAYER2_WIN ) {
			if (message.getContent().equals(OMOK_START)) {
				if (sender.getID().equals(id1) || sender.getID().equals(id2)) {
					boolean isPlayer1 = sender.getID().equals(id1) ? true : false;
					boolean startSuccess = og.start(isPlayer1);
					System.out.println("startSuccess: " + startSuccess);
					displayBoard(channel);
				}
			}
		} else if (og.getState() == OmokGame.PLAYER1_TURN) {
			if (message.getContent().equals(OMOK_RESIGN) && (sender.getID().equals(id1) || sender.getID().equals(id2))) { // resignation
				boolean isPlayer1 = sender.getID().equals(id1) ? true : false;
				boolean resignSuccess = og.resign(isPlayer1);
				System.out.println("resignSuccess: " + resignSuccess);
				sendMessage("Player " + (isPlayer1 ? 1 : 2) + " has resigned.", channel);
				resetGame();
			} else { // check for making move
				if (sender.getID().equals(id1)) {
					int[] move = parseMove(message.getContent());
					if (move[0] != -1) { // valid move
						boolean makeMoveSuccess = og.makeMove(move[1], move[0], true);
						System.out.println("makeMoveSuccess: " + makeMoveSuccess);
						if (!makeMoveSuccess) {
							sendMessage("Invalid move!", channel);
						} else {
							displayBoard(channel);
							if (og.getState() == OmokGame.PLAYER1_WIN) {
								sendMessage("Player 1 has won!", channel);
								resetGame();
							}
						}
					}
				}
			}
		} else if (og.getState() == OmokGame.PLAYER2_TURN) {
			if (message.getContent().equals(OMOK_RESIGN) && (sender.getID().equals(id1) || sender.getID().equals(id2))) { // resignation
				boolean isPlayer1 = sender.getID().equals(id1) ? true : false;
				boolean resignSuccess = og.resign(isPlayer1);
				System.out.println("resignSuccess: " + resignSuccess);
				sendMessage("Player " + (isPlayer1 ? 1 : 2) + " has resigned.", channel);
				resetGame();
			} else { // check for making move
				if (sender.getID().equals(id2)) {
					int[] move = parseMove(message.getContent());
					if (move[0] != -1) { // valid move
						boolean makeMoveSuccess = og.makeMove(move[1], move[0], false);
						System.out.println("makeMoveSuccess: " + makeMoveSuccess);
						if (!makeMoveSuccess) {
							sendMessage("Invalid move!", channel);
						} else {
							displayBoard(channel);
							if (og.getState() == OmokGame.PLAYER2_WIN) {
								sendMessage("Player 2 has won!", channel);
								resetGame();
							}
						}
					}
				}
			}
		}
	}
	
	private void resetGame() {
		//og.reset();
		og = null;
		id1 = null;
		id2 = null;
	}
	
	public void displayBoard(IChannel channel) {
		sendMessage("`" + og.toString() + "`", channel);
	}
	
	public boolean sendMessage(String message, IChannel channel) {
		try {
			//Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
			new MessageBuilder(this.client).withChannel(channel).withContent(message).build();
			return true;
		} catch (HTTP429Exception e) { //HTTP429Exception thrown. The bot is sending messages too quickly!
			System.err.print("Sending messages too quickly!");
			e.printStackTrace();
			return false;
		} catch (DiscordException e) { //DiscordException thrown. Many possibilities.
			System.err.print(e.getErrorMessage()); //Print the error message sent by Discord
			e.printStackTrace();
			return false;
		} catch (MissingPermissionsException e) { //MissingPermissionsException thrown. The bot doesn't have permission to send the message!
			System.err.print("Missing permissions for channel!");
			e.printStackTrace();
			return false;
		}
	}
	
	/* Gets the integer coordinates from the input. */
	private static int[] parseMove(String input) {
		input = input.trim();
		String[] parts = input.split("\\s+");
		if (parts.length != 2) { // not 2 parts
			//System.out.println("not 2 parts");
			return new int[]{ -1, -1 };
		}
		if (parts[0].length() != 1) {
			//System.out.println("not a single char");
			return new int[]{ -1, -1 };
		}
		char first = parts[0].toUpperCase().charAt(0); // WITH THIS IMPLEMENTATION THE BOARD CAN'T BE TOO BIG 
		int second; 								   // OR LOWERCASE CHARACTERS (HIGHER ASCII VALUES) WILL CONVERT TO UPPERCASE 
		try {
			second = Integer.parseInt(parts[1]);
		} catch (Exception e) { // second part is not a number
			//System.out.println("not a number");
			return new int[]{ -1, -1 };
		}
		
		return new int[]{ (int)first - 65, second - 1 };
	}

}

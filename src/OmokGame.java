
public class OmokGame {
	
	public static final int INIT = 0;
	public static final int PLAYER1_TURN = 1;
	public static final int PLAYER2_TURN = 2;
	public static final int PLAYER1_WIN = 3;
	public static final int PLAYER2_WIN = 4;
	public static final int TERMINATED = 5;
	
	private int state;
	private Board b;
	private Player player1;
	private Player player2;
	private String errMsg;
	
	public OmokGame() {
		b = new Board();
		player1 = new Player("player1", "X");
		player2 = new Player("player2", "O");
		state = INIT;
	}
	
	public void swapPlayer1(Player p) {
		player1 = p;
	}
	
	public void swapPlayer2(Player p) {
		player2 = p;
	}
	
	public int getState() {
		return state;
	}
 	
	/* activated when the game receives input from either player.
	 * 
	 * isPlayer1 is boolean for telling which player the input came from
	 * input is the text the player entered
	 * 
	 * Invalid inputs will not change state
	 * Returns new state upon completion (or current state again if nothing changed)
	 */
	public int onInput(boolean isPlayer1, String input) {
		if (state == INIT) {
			if (input.equals("start")) { // BEGIN
				state = isPlayer1 ? PLAYER2_TURN : PLAYER1_TURN;
			}
		} else if (state == PLAYER1_TURN) {
			if (input.equals("resign")) { // resignation
				state = isPlayer1 ? PLAYER2_WIN : PLAYER1_WIN;
			} else if (isPlayer1) {
				int[] move = parseMove(input);
				if (move[0] != -1) { // valid move
					b.placePiece(1, move[0], move[1]);
					if (b.checkConnections(move[0], move[1])) { // has this move won the game?
						state = PLAYER1_WIN;
						System.out.println("Player 1 has won");
					} else {
						state = PLAYER2_TURN;
						System.out.println("changed to player2's turn");
					}
				}
			}
		} else if (state == PLAYER2_TURN) {
			if (input.equals("resign")) { // resignation
				state = isPlayer1 ? PLAYER2_WIN : PLAYER1_WIN;
			} else if (!isPlayer1) {
				int[] move = parseMove(input);
				if (move[0] != -1) { // valid move
					b.placePiece(-1, move[0], move[1]);
					if (b.checkConnections(move[0], move[1])) { // has this move won the game?
						state = PLAYER2_WIN;
						System.out.println("Player 2 has won");
					} else {
						state = PLAYER1_TURN;
						System.out.println("changed to player2's turn");
					}
				}
			}
		} else if (state == PLAYER1_WIN) {
			if (input.equals("restart")) { // restart with losing player's turn
				b.clear();
				state = PLAYER2_TURN;
			}
		} else if (state == PLAYER2_WIN) {
			if (input.equals("restart")) {
				b.clear();
				state = PLAYER1_TURN;
			}
		}
		return state;
	}
	
	private int[] parseMove(String input) {
		input = input.trim();
		String[] parts = input.split(" ");
		if (parts.length != 2) { // not 2 parts
			System.out.println("not 2 parts");
			return new int[]{ -1, -1 };
		}
		int first;
		int second;
		try {
			first = Integer.parseInt(parts[0]);
			second = Integer.parseInt(parts[1]);
		} catch (Exception e) { // either part is not a number
			System.out.println("not a number");
			return new int[]{ -1, -1 };
		}
		if (first < 0 || first >= b.getBoardLength() || second < 0 || second >= b.getBoardWidth()) { // out of bounds
			System.out.println("out of bound");
			return new int[]{ -1, -1 };
		}
		if (b.getPieceAt(first, second) != 0) { // already has existing piece
			System.out.println("existing piece");
			return new int[]{ -1, -1 };			
		}
		return new int[]{ first, second };
	}
	
	public String toString() {
		String answer = player1.getID() + ": " + player1.getPiece() + "\n" + player2.getID() + ": " + player2.getPiece();
		answer += "\n\n";
		int[][] omokBoard = b.getBoard();
		answer += "  ";
		for (int j = 0; j < omokBoard[0].length; j++) {
			answer += j + " ";
		}
		answer += "\n";
		for (int i = 0; i < omokBoard.length; i++) {
			answer += i + " ";
			for (int j = 0; j < omokBoard[i].length; j++) {
				if (omokBoard[i][j] == 0) {
					answer += "_ ";
				} else{
					answer += (omokBoard[i][j] == 1 ? player1.getPiece() : player2.getPiece()) + " ";
				}
			}
			answer += "\n";
		}
		return answer;
	}
}

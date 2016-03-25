import java.util.Scanner;
public class OmokDriver {

	public static void main(String[] args) {
		OmokGame og = new OmokGame();
		Scanner kboard = new Scanner(System.in);
		String input = "start";
		boolean isPlayer1 = false;
		int state = og.onInput(isPlayer1, input);
		while (state != OmokGame.PLAYER1_WIN && state != OmokGame.PLAYER2_WIN ) {
			System.out.println(og);
			System.out.println(state);
			isPlayer1 = (state == OmokGame.PLAYER1_TURN);
			System.out.print("Player " + (isPlayer1 ? "1" : "2") + "'s turn: ");
			input = kboard.nextLine();
			state = og.onInput(isPlayer1, input);
		}
		System.out.println("hi");
	}
	
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
	
	/* Gets the integer coordinates from the input. */
	private int[] parseMove(String input) {
		input = input.trim();
		String[] parts = input.split("\\s+");
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
		
		return new int[]{ first, second };
	}

}

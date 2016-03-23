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

}

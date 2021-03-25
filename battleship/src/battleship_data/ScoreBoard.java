/*
 * Name: ScoreBoard.java
 * Description: Monitor scores of a game, and modify the highscore document.
 */

package battleship_data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class ScoreBoard {

	private final int[] CURRENT_SCORES = new int[2]; // [0]: player 1 score; [1] player 2 score

	// [0] is player 1 score, [1] is player 2 score
	public ScoreBoard() {
		CURRENT_SCORES[0] = 0;
		CURRENT_SCORES[1] = 0;
	}

	public int getScore(int player) {
		return CURRENT_SCORES[player - 1];
	}

	public void addScoreToPlayer(int score, int player) {
		CURRENT_SCORES[player - 1] += score;
	}

	// Return 0 if tie, otherwise return player number
	public int getWinner() {
		if (CURRENT_SCORES[0] > CURRENT_SCORES[1]) {
			return 1;
		} else if (CURRENT_SCORES[0] < CURRENT_SCORES[1]) {
			return 2;
		} else {
			return 0;
		}
	}

	// Update highscore.txt based on current score
	public void evaluateHighscore() {

		// Read highscore.txt and generate a nested list of scores
		ArrayList<ArrayList<String>> highscores = new ArrayList<ArrayList<String>>();
		try(BufferedReader br = new BufferedReader(new FileReader("highscores.txt"))) {
			br.readLine();
			for (int i = 0; i < 10; i++) {
				ArrayList<String> score = new ArrayList<String>();
				String line = br.readLine();
				String[] items = line.split("\t");
				score.add(items[1]);
				score.add(items[2]);
				highscores.add(score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Add the scores of both players to the list of scores
		ArrayList<String> p1score = new ArrayList<String>();
		p1score.add("Player 1");
		p1score.add(Integer.toString(CURRENT_SCORES[0]));
		ArrayList<String> p2score = new ArrayList<String>();
		p2score.add("Player 2");
		p2score.add(Integer.toString(CURRENT_SCORES[1]));
		highscores.add(p1score);
		highscores.add(p2score);

		// Reverse sort the list using the score of the players as the key
		highscores.sort((ArrayList<String> score1, ArrayList<String> score2)->
		Integer.parseInt(score2.get(1))-Integer.parseInt(score1.get(1)));

		// Erase the old file, put the first 10 scores of the list to the document
		try (FileWriter fw = new FileWriter("highscores.txt", false)) {
			fw.write("No\tPlayer\tScore" + System.lineSeparator());
			for (int i = 1; i < 11; i++) {
				fw.write(Integer.toString(i) + "\t" + highscores.get(i-1).get(0) + "\t" + 
						highscores.get(i-1).get(1) + System.lineSeparator());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Reset highscore.txt to default (all players are N/A; all scores are 0).
	public static void resetHighscores() {
		try (FileWriter fw = new FileWriter("highscores.txt", false)) {
			fw.write("No\tPlayer\tScore" + System.lineSeparator());
			for (int i = 1; i < 11; i++) {
				fw.write(Integer.toString(i) + "\tN\\A\t0" + System.lineSeparator());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
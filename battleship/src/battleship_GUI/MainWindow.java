/*
 * Name: MainWindow.java
 * Description: Generate a new game with new board and score board.
 * Each button in the map is listened by the cell that contains it in order to reveal the colors,
 * as well as by the main window to record their coordinates, so that they can be passed
 * to board and score board.
 */

package battleship_GUI;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import battleship_data.Board;
import battleship_data.ScoreBoard;
import battleship_data.Ship;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MainWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	// Elements that need to be accessed by ActionListener / changed dynamically
	private final int PLAYER_BONUS; // Add to player 2 if the player hits a ship; either 0 or 5
	private final Board CURRENT_BOARD;
	private final ScoreBoard CURRENT_SCOREBOARD = new ScoreBoard();

	private boolean bGameOver;
	private int noOfShipsLeft;
	private int playerNo; // Either 1 or 2; alternates each round
	private JLabel p1Score;
	private JLabel turn;
	private JLabel p2Score;
	private JLabel shipLeftLabel;
	private JLabel notification;

	// Generate a game with file
	public MainWindow(File file, int playerBonus) throws FileNotFoundException, IllegalArgumentException {
		super();
		CURRENT_BOARD = new Board(file);
		this.PLAYER_BONUS = playerBonus;
		MakeWindow();		
	}

	// Generate a game randomly with a certain board size
	public MainWindow(int sizeX, int sizeY, int playerBonus) {
		super();
		CURRENT_BOARD = new Board(sizeX, sizeY);
		this.PLAYER_BONUS = playerBonus;
		MakeWindow();
	}

	void MakeWindow() {

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("BATTLESHIP");

		bGameOver = false;
		playerNo = 1;
		noOfShipsLeft = CURRENT_BOARD.getNumberOfShips();

		setMinimumSize(new Dimension(800, 650));
		setPreferredSize(getMinimumSize());

		// Top panel contains the scores, player turn, high score button and quit button
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(2,5,10,10));
		top.setMaximumSize(getPreferredSize());

		// Components of the top panel
		JButton highScoreButton = new JButton("High Scores");
		JLabel p1ScoreLabel = new JLabel("Player 1 Score:", SwingConstants.CENTER);
		JLabel turnLabel = new JLabel("Turn:", SwingConstants.CENTER);
		JLabel p2ScoreLabel = new JLabel("Player 2 Score:", SwingConstants.CENTER);
		JButton quitButton = new JButton("Quit Game");
		shipLeftLabel = new JLabel("Number of ship left: " + Integer.toString(noOfShipsLeft), SwingConstants.CENTER);
		p1Score = new JLabel("0", SwingConstants.CENTER);
		turn = new JLabel("Player 1", SwingConstants.CENTER);
		p2Score = new JLabel("0", SwingConstants.CENTER);
		notification = new JLabel("", SwingConstants.CENTER);

		highScoreButton.addActionListener(this);
		highScoreButton.setActionCommand("highscore");
		quitButton.addActionListener(this);
		quitButton.setActionCommand("quit");

		// Change fonts of the top panel labels
		p1ScoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		turnLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		p2ScoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		p1Score.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
		turn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
		p2Score.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));

		top.add(highScoreButton);
		top.add(p1ScoreLabel);
		top.add(turnLabel);
		top.add(p2ScoreLabel);
		top.add(quitButton);
		top.add(shipLeftLabel);
		top.add(p1Score);
		top.add(turn);
		top.add(p2Score);
		top.add(notification);

		// Map panel contains cells that represent coordinates on the board.
		JPanel map = new JPanel();
		map.setLayout(new GridLayout(CURRENT_BOARD.getBoardSizeY(),CURRENT_BOARD.getBoardSizeX(),10,10));

		// Get the ship Object from shipMap and pass it to generate a cell
		for (int coordY = 0; coordY < CURRENT_BOARD.getBoardSizeY(); coordY++) {
			for (int coordX = 0; coordX < CURRENT_BOARD.getBoardSizeX(); coordX++) {
				Ship thisShip = CURRENT_BOARD.getShip(coordX, coordY);
				Cell cell = new Cell(coordX, coordY, thisShip);
				cell.addAnotherListener(this);
				cell.addAnotherListener(cell);
				map.add(cell.getCell());
			}
		}

		add(Box.createVerticalStrut(10));
		add(top);
		add(Box.createVerticalStrut(10));
		add(map);
		add(Box.createVerticalStrut(10));

		pack();
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("quit")) { // Quit Button
			int choice = JOptionPane.showOptionDialog(this,"Are you sure? The game is not finished.", 
					"Quit Game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
			if (choice == JOptionPane.YES_OPTION) {
				setVisible(false);
			}

		} else if (e.getActionCommand().equals("highscore")) { // High Scores Button

			// Read highscore.txt and pop up a window
			try(BufferedReader br = new BufferedReader(new FileReader("highscores.txt"))) {
				String highscores = "";
				for (int i = 0; i < 11; i++) {
					highscores += br.readLine() + "\n";
				}
				JTextArea ta = new JTextArea(highscores); // For better formatting
				ta.setBackground(null);
				ta.setEditable(false);
				JOptionPane.showMessageDialog(this, ta, "HIGHSCORE", JOptionPane.PLAIN_MESSAGE);
			} catch (Exception ee) {
				ee.printStackTrace();
			}

		} else { // Map buttons

			// Get coordinates from the button
			String stringCoord = e.getActionCommand();
			int coordX = Integer.parseInt(stringCoord.split("\t")[0]);
			int coordY = Integer.parseInt(stringCoord.split("\t")[1]);
			CURRENT_BOARD.hit(coordX, coordY);

			int scoreToAdd = 0;
			Ship ship = CURRENT_BOARD.getShip(coordX, coordY);

			// Calculate score
			if (ship != null) {
				if (playerNo == 2) {
					// Second player advantage is 0 or 5 depending on user selection
					scoreToAdd = ship.getScore() * ship.isDown() + PLAYER_BONUS;
				} else {
					scoreToAdd = ship.getScore() * ship.isDown();
				}
			}

			// Calculate number of ships left and generate notifications
			if (ship != null && ship.isDown() == 2) {
				noOfShipsLeft--;
				shipLeftLabel.setText("Number of ship left: " + Integer.toString(noOfShipsLeft));
				notification.setText("The " + ship.getShipType().name().toLowerCase() + " has sunk!");
				revalidate();
			} else {
				notification.setText("");
				revalidate();
			}

			// Update scores
			CURRENT_SCOREBOARD.addScoreToPlayer(scoreToAdd, playerNo);
			p1Score.setText(Integer.toString(CURRENT_SCOREBOARD.getScore(1)));
			p2Score.setText(Integer.toString(CURRENT_SCOREBOARD.getScore(2)));
			revalidate();

			// If game is over, report the winner and ask whether to start a new game
			bGameOver = CURRENT_BOARD.allHit();
			if (bGameOver) {
				CURRENT_SCOREBOARD.evaluateHighscore();
				String winMessage = "";
				switch (CURRENT_SCOREBOARD.getWinner()) {
				case 0: {winMessage = "Tied.\n"; break;}
				case 1: {winMessage = "Player 1 is the winner!\n"; break;}
				case 2: {winMessage = "Player 2 is the winner!\n"; break;}
				}
				winMessage += "Want to start a new game?";
				Object[] options = {"New Game", "Quit"};
				int choice = JOptionPane.showOptionDialog(this, winMessage, "Game Over",  JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				if (choice == JOptionPane.YES_OPTION) {new IntroWindow();}

				setVisible(false);
			}

			// Update player turn
			changePlayer();
			turn.setText("Player " + Integer.toString(playerNo));
			revalidate();
		}


	}

	void changePlayer() {
		if (playerNo == 1) {
			playerNo = 2;
		} else {
			playerNo = 1;
		}
	}

}
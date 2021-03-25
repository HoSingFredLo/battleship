/*
 * Name: IntroWindow.java
 * Description: Opens a window for starting the game.
 */

package battleship_GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import battleship_data.Board;
import battleship_data.ScoreBoard;

public class IntroWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	// Elements that need to be accessed by ActionListener / changed dynamically
	private int playerBonus = 0; // Add to player 2 if the player hits a ship; either 0 or 5
	private File chosenFile; // The path to file the user has chosen to start the game
	private CardLayout optionsCards;
	private JTextField chosenFileText;
	private JPanel startGamePanel;
	private JSpinner rowSpinner; // 5 to 10; number of rows if the map is randomly generated
	private JSpinner colSpinner; // 5 to 10; number of columns if the map is randomly generated

	public IntroWindow() {

		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10,10));
		setTitle("BATTLESHIP");
		setResizable(false);

		chosenFile = null;

		// Top panel contains the game title
		JLabel gameTitle = new JLabel("Battleship", SwingConstants.CENTER);
		gameTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 36));


		// Bottom panel contains four buttons that does not start the game
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 4, 10, 10));


		JButton rules = new JButton("Rules");
		JButton highScore = new JButton("High Scores");
		JButton resetHighScore = new JButton("Reset High Scores");
		JButton exit = new JButton("Exit");

		rules.addActionListener(this);
		rules.setActionCommand("rules");
		highScore.addActionListener(this);
		highScore.setActionCommand("highscore");
		resetHighScore.addActionListener(this);
		resetHighScore.setActionCommand("reset-highscore");
		exit.addActionListener(this);
		exit.setActionCommand("exit");

		buttonsPanel.add(rules);
		buttonsPanel.add(highScore);
		buttonsPanel.add(resetHighScore);
		buttonsPanel.add(exit);

		JPanel centralPanel = new JPanel();
		centralPanel.setLayout(new GridLayout(1, 2));
		centralPanel.add(makeOptionsPanel());
		centralPanel.add(makeStartGamePanel());

		add(gameTitle, BorderLayout.NORTH);
		add(centralPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}

	JPanel makeOptionsPanel() {

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(2, 1));

		// Choose Layout Source Panel
		JPanel layoutPanel = new JPanel();
		layoutPanel.setLayout(new GridLayout(4, 1));

		JLabel layoutSource = new JLabel("  Choose Layout Source:");
		layoutSource.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

		JRadioButton fileOption = new JRadioButton("Import from file", true);
		JRadioButton numberOption = new JRadioButton("Generate random ship placement");

		fileOption.addActionListener(this);
		fileOption.setActionCommand("file-as-source");
		numberOption.addActionListener(this);
		numberOption.setActionCommand("no-source");

		// Limit options to 1
		ButtonGroup layoutSourceOptions = new ButtonGroup();
		layoutSourceOptions.add(fileOption);
		layoutSourceOptions.add(numberOption);

		layoutPanel.add(layoutSource);
		layoutPanel.add(fileOption);
		layoutPanel.add(numberOption);

		// Choose Scoring System Panel
		JPanel scoringPanel = new JPanel();
		scoringPanel.setLayout(new GridLayout(4, 1));

		JLabel ifPlayerBonus = new JLabel("  Choose Scoring System:");
		ifPlayerBonus.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

		JRadioButton evenOption = new JRadioButton("Points are independent of player", true);
		JRadioButton oddOption = new JRadioButton("Player 2 gets more points each hit");
		evenOption.addActionListener(this);
		evenOption.setActionCommand("even-game");
		oddOption.addActionListener(this);
		oddOption.setActionCommand("odd-game");

		// Limit options to 1
		ButtonGroup playerBonusOptions = new ButtonGroup();
		playerBonusOptions.add(evenOption);
		playerBonusOptions.add(oddOption);

		scoringPanel.add(ifPlayerBonus);
		scoringPanel.add(evenOption);
		scoringPanel.add(oddOption);

		optionsPanel.add(layoutPanel);
		optionsPanel.add(scoringPanel);

		return optionsPanel;
	}

	JPanel makeStartGamePanel() {

		startGamePanel = new JPanel();
		optionsCards = new CardLayout(2, 1);
		startGamePanel.setLayout(optionsCards);

		// Start game with file chosen
		JPanel fileTab = new JPanel();
		fileTab.setLayout(new BorderLayout());

		chosenFileText = new JTextField();
		chosenFileText.setEditable(false);

		JButton chooseFileButton = new JButton("Choose File");
		JButton startGameWithFile = new JButton("Start Game");

		chooseFileButton.addActionListener(this);
		chooseFileButton.setActionCommand("file");
		startGameWithFile.addActionListener(this);
		startGameWithFile.setActionCommand("start-file");

		fileTab.add(chosenFileText, BorderLayout.CENTER);
		fileTab.add(chooseFileButton, BorderLayout.EAST);
		fileTab.add(startGameWithFile, BorderLayout.SOUTH);

		// Start game with random map chosen
		JPanel numberTab = new JPanel();
		numberTab.setLayout(new BorderLayout());

		JPanel numberPanel = new JPanel();
		numberPanel.setLayout(new GridLayout(2, 2));

		JLabel chooseRow = new JLabel("Choose Row:");
		JLabel chooseColumn = new JLabel("Choose Column:");

		// Create spinners for rows and columns, limit between 5-10
		String[] rowAndColumnOptions = new String[6];
		for (int i = 5; i < 11; i++) {
			rowAndColumnOptions[i-5] = Integer.toString(i);
		}
		SpinnerListModel rowModel = new SpinnerListModel(rowAndColumnOptions);
		SpinnerListModel colModel = new SpinnerListModel(rowAndColumnOptions);
		rowSpinner = new JSpinner(rowModel);
		colSpinner = new JSpinner(colModel);

		numberPanel.add(chooseRow);
		numberPanel.add(rowSpinner);
		numberPanel.add(chooseColumn);
		numberPanel.add(colSpinner);

		JButton startGameRandom = new JButton("Start Game");

		startGameRandom.addActionListener(this);
		startGameRandom.setActionCommand("start-random");

		numberTab.add(numberPanel, BorderLayout.CENTER);
		numberTab.add(startGameRandom, BorderLayout.SOUTH);

		startGamePanel.add(fileTab, "fileTab");
		startGamePanel.add(numberTab, "numberTab");

		return startGamePanel;
	}

	// Open a JFileChooser window to choose file
	void getFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Battleship Layout File (*.txt)", "txt");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			chosenFile = chooser.getSelectedFile();
			chooser.setVisible(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()) {
		case "file": { // Choose file button
			getFile();
			if (chosenFile != null) {chosenFileText.setText(chosenFile.toString());}
			revalidate();
			break;
		} 
		case "start-file": { // Start game with file button
			if (chosenFile == null) {
				JOptionPane.showMessageDialog(this, "Please select a file.");
			} else {
				try {
					new Board(chosenFile);
					new MainWindow(chosenFile, playerBonus);
					this.setVisible(false);
				} catch (Exception ee) {
					ee.printStackTrace();
					if (ee.getClass().getName() == "java.lang.IllegalArgumentException") {
						JOptionPane.showMessageDialog(this, "Not a valid layout.\n" + ee.getMessage());
					} else {
						JOptionPane.showMessageDialog(this, "Not a valid layout file.");
					}

				}	
			}
			break;
		}
		case "start-random": { // Start game with random layout button
			int rows = Integer.parseInt((String) rowSpinner.getValue());
			int columns = Integer.parseInt((String) colSpinner.getValue());
			new MainWindow(rows, columns, playerBonus);
			this.setVisible(false);
			break;
		}
		case "file-as-source": { // Choose file as layout source
			optionsCards.show(startGamePanel, "fileTab");
			break;
		}
		case "no-source": { // Choose to generate random layout
			optionsCards.show(startGamePanel, "numberTab");
			break;
		}
		case "even-game": { // No second player bonus
			playerBonus = 0;
			break;
		}
		case "odd-game": { // With second player bonus
			playerBonus = 5;
			break;
		}
		case "rules": {
			try (Scanner sc = new Scanner(new FileReader("rules.txt"));) {
				String rules = "";
				while (sc.hasNextLine()) {
					rules += sc.nextLine() + "\n";
				}
				JOptionPane.showMessageDialog(this, rules, "RULES", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			break;
		}
		case "highscore": {
			try(BufferedReader br = new BufferedReader(new FileReader("highscores.txt"))) {
				String highscores = "";
				for (int i = 0; i < 11; i++) {
					highscores += br.readLine() + "\n";
				}
				JTextArea ta = new JTextArea(highscores);
				ta.setBackground(null);
				ta.setEditable(false);
				JOptionPane.showMessageDialog(this, ta, "HIGHSCORE", JOptionPane.PLAIN_MESSAGE);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			break;
		}
		case "reset-highscore": {
			int choice = JOptionPane.showOptionDialog(this,"Are you sure? This action is irreversible.", 
					"Reset Highscore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
			if (choice == JOptionPane.YES_OPTION) {
				ScoreBoard.resetHighscores();
			}
			break;
		}
		case "exit": {
			setVisible(false);
			break;
		}
		}

	}
}




/*
 * Name: StartGame.java
 * Description: Generate the intro window in the EDT.
 */

package battleship_run;

import javax.swing.SwingUtilities;

import battleship_GUI.IntroWindow;

public class StartGame {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new IntroWindow();
			}
		});
	}
}
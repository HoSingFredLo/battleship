/*
 * Name: Cell.java
 * Description: Generate a cell that corresponds to a coordinate, which is also an action
 * listener for the button it contains, so that it can change the panels in the card layout
 * of the panel of the cell.
 */

package battleship_GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import battleship_data.Ship;

public class Cell implements ActionListener {

	private final CardLayout CL = new CardLayout();
	private final JPanel CELL = new JPanel(CL); // Contains the top card (button) and the bottom card (color)
	private final JButton BUTTON = new JButton(""); 

	public Cell(int coordX, int coordY, Ship ship) {
		JPanel top = makeTopPanel(coordX, coordY);
		JPanel bottom = makeBottomPanel(ship);
		CELL.add(top, "top");
		CELL.add(bottom, "bottom");	
	}

	public void actionPerformed(ActionEvent e) {
		CL.next(CELL); // Hide the button, show the color
	}

	void addAnotherListener(ActionListener al) {
		BUTTON.addActionListener(al);
	}

	// The buttons
	JPanel makeTopPanel(int coordX, int coordY) {
		JPanel panel = new JPanel(new BorderLayout());
		BUTTON.setActionCommand(Integer.toString(coordX) + "\t" + Integer.toString(coordY));
		panel.add(BUTTON, BorderLayout.CENTER);
		return panel;
	}

	// The colors
	JPanel makeBottomPanel(Ship ship) {
		Color c;
		if (ship == null) {
			c = Color.BLUE;
		} else {
			c = ship.getColor();
		}

		JPanel panel = new JPanel();
		panel.setBackground(c);
		return panel;
	}

	JPanel getCell() {
		return CELL;
	}
}
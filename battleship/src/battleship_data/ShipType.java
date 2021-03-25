/*
 * Name: ShipType.java
 * Description: Define the properties of different ship types, including length, scores and colors.
 */

package battleship_data;

import java.awt.Color;

public enum ShipType {

	CARRIER (5, 10, Color.RED),
	BATTLESHIP (4, 15, Color.YELLOW),
	SUBMARINE (3, 25, Color.GREEN),
	DESTROYER (2, 30, Color.WHITE);

	private final int length; // The number of coordinates the ship spans on the map
	private final int score; // The amount of score added to the player if it is hit (before bonus)
	private final Color color; // The color of the tile after the coordinate containing the ship is hit

	ShipType (int length, int score, Color color) {
		this.length = length;
		this.score = score;
		this.color = color;
	}

	int getLength() {
		return length;
	}

	int getScore() {
		return score;
	}

	Color getColor() {
		return color;
	}

}

/*
 * Name: Ship.java
 * Description: Defines the properties and behavior of ship objects, which the latter is
 * mainly determining whether it is sunk. 
 */

package battleship_data;

import java.awt.Color;

public class Ship {

	private final int MY_SHIP_NUMBER; // Indicates when the ship is generated among all ships
	private final ShipType MY_SHIP_TYPE; // Points to a ship type
	private int remainingParts; // Every time a ship is hit, it has less coordinates left to be hit

	public Ship(ShipType type, int shipNumber) {
		this.MY_SHIP_TYPE = type;
		this.MY_SHIP_NUMBER = shipNumber;
		this.remainingParts = type.getLength();
	}

	public ShipType getShipType() {
		return MY_SHIP_TYPE;
	}

	public Color getColor() {
		return MY_SHIP_TYPE.getColor();
	}

	public int getScore() {
		return MY_SHIP_TYPE.getScore();
	}

	public int getShipNumber() {
		return MY_SHIP_NUMBER;
	}

	// When the ship if hit, it has less parts up
	public void hit() {
		remainingParts--;
	}


	// Return the 2x bonus when the ship is sunk
	public int isDown() {
		if (remainingParts == 0) {
			return 2;
		}
		return 1;
	}

}
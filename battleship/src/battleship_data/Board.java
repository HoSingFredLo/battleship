/*
 * Name: Board.java
 * Description: 
 * A board object is generated with a file object or board size.
 * For the former, it checks whether the layout is valid, which means the ships
 * are of correct size, shape, not out of bound and not overlapping.
 * For the latter, it generates ships randomly and put then on the map.
 * 
 * It then documents the locations of the ships as "ShipMap", where it passes the hit
 * signal to the ship objects, and the locations that are hit as "hitMap". where it
 * determines whether the game is over.
 */

package battleship_data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Board {

	private final int NO_OF_SHIPS; // Number of ships on the board
	private final int BOARD_SIZE_X; // The width of the map; horizontal length
	private final int BOARD_SIZE_Y; // The height of the map; vertical length
	private final boolean[][] HIT_MAP; // coordinates; true means no ship or ship has been hit on that coordinate
	private final Ship[][] SHIP_MAP; // coordinates; points to the ship object on each coordinate

	// Generate random ship placement for a board of certain size
	public Board(int sizeX, int sizeY) {

		BOARD_SIZE_X = sizeX;
		BOARD_SIZE_Y = sizeY;

		HIT_MAP = new boolean[BOARD_SIZE_Y][BOARD_SIZE_X];
		SHIP_MAP = new Ship[BOARD_SIZE_Y][BOARD_SIZE_X];

		// Initialize the board where there are no ships
		for (int i = 0; i < BOARD_SIZE_Y; i++) {
			for (int j = 0; j < BOARD_SIZE_X; j++) {
				HIT_MAP[i][j] = true;
				SHIP_MAP[i][j] = null;
			}
		}

		// Determine the number of ships to put on map
		NO_OF_SHIPS = (BOARD_SIZE_X + BOARD_SIZE_Y) / 4;
		int shipNumber = 1;

		char[] directions = {'n', 'e', 's', 'w'};

		Random rand = new Random();

		// Convert random integers as array index to ship types
		ShipType[] intToShip = {ShipType.CARRIER, ShipType.BATTLESHIP, ShipType.SUBMARINE, ShipType.DESTROYER};

		while (shipNumber <= NO_OF_SHIPS) {
			int coordX = rand.nextInt(BOARD_SIZE_X);
			int coordY = rand.nextInt(BOARD_SIZE_Y);
			ShipType type = intToShip[rand.nextInt(4)];

			// "Grow" ships in four possible directions. If the ship is valid, try to put the ship
			// on the map. If no overlapping, the ship generation is successful, move onto the next ship.
			for (char direction : directions) {
				ArrayList<ArrayList<Integer>> coordinates = growShip(coordX, coordY, direction, type);
				if (isValidShip(type, coordinates) && putShipOnMap(type, shipNumber, coordinates)) {
					shipNumber++;
					break;
				}
			}
		}
	}

	// Generate ship placement base on file
	public Board(File file) throws FileNotFoundException, IllegalArgumentException {

		int noOfShipsInFile = 0;

		try (Scanner sc = new Scanner(new FileReader(file))) {

			BOARD_SIZE_X = BOARD_SIZE_Y = Integer.parseInt(sc.nextLine());

			// Check if board size is between 5 to 10
			if (BOARD_SIZE_X < 5) {
				throw new IllegalArgumentException("The board is too small! Please use a number between 5 to 10.");
			} else if (BOARD_SIZE_X > 10) {
				throw new IllegalArgumentException("The board is too big! Please use a number between 5 to 10.");
			}

			// Count number of ships in the file
			while(sc.hasNextLine()) {
				noOfShipsInFile++;
				sc.nextLine();
			}

			// Check if number of ships is within range
			int minimumShipNumber = BOARD_SIZE_X / 2 - 1;
			int maximumShipNumber = BOARD_SIZE_X / 2 + 1;
			String noOfShipWarning = "Please only put " + minimumShipNumber + " to " + maximumShipNumber + " ships"
					+ " for a(n) " + BOARD_SIZE_X + "x" + BOARD_SIZE_Y + " board.";

			if (noOfShipsInFile > maximumShipNumber) {
				throw new IllegalArgumentException("Too many ships! " + noOfShipWarning);
			} else if (noOfShipsInFile < minimumShipNumber) {
				throw new IllegalArgumentException("Too few ships! " + noOfShipWarning);
			}
		}

		NO_OF_SHIPS = noOfShipsInFile;

		String[] allShipsInfo = new String[noOfShipsInFile];

		// Restart the scanner
		try (Scanner sc = new Scanner(new FileReader(file))) {
			sc.nextLine(); // Skip first line

			for (int i = 0; i < noOfShipsInFile; i++) {
				allShipsInfo[i] = sc.nextLine();
			}	
		}

		HIT_MAP = new boolean[BOARD_SIZE_Y][BOARD_SIZE_X];
		SHIP_MAP = new Ship[BOARD_SIZE_Y][BOARD_SIZE_X];

		// Initialize the board where there are no ships
		for (int i = 0; i < BOARD_SIZE_Y; i++) {
			for (int j = 0; j < BOARD_SIZE_X; j++) {
				HIT_MAP[i][j] = true; // true means no ship or ship has been hit
				SHIP_MAP[i][j] = null;
			}
		}

		for (int i = 0; i < noOfShipsInFile; i++) {

			String l = allShipsInfo[i];
			String[] shipInfo = l.split(";");

			// Get ship type
			ShipType thisShipType = ShipType.valueOf(shipInfo[0].toUpperCase());


			ArrayList<ArrayList<Integer>> coordinates = new ArrayList<ArrayList<Integer>>();
			for (int j = 1; j < shipInfo.length; j++) {
				String[] stringCoord = shipInfo[j].split("\\*");
				ArrayList<Integer> coordinate = new ArrayList<>();
				for (String s : stringCoord) {
					coordinate.add(Integer.parseInt(s)-1); // Convert coordinates to 0-based
				}
				coordinates.add(coordinate);
			}

			// Generate a new ship if it is valid
			if (isValidShip(thisShipType, coordinates)) {
				if (!putShipOnMap(thisShipType, i + 1, coordinates)) {
					throw new IllegalArgumentException("Overlapping ships.");
				}
			} else {
				throw new IllegalArgumentException("Ship number " + Integer.toString(i + 1) + " is not a valid ship.");
			}
		}
	}

	// Pass a coordinate, the function expands the coordinates in four possible directions: n, e, s, w
	// depending on the ship type, then return a list of coordinates.
	ArrayList<ArrayList<Integer>> growShip(int coordX, int coordY, char direction, ShipType type) {

		ArrayList<ArrayList<Integer>> coordinates = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> coordinate = new ArrayList<Integer>();
		coordinate.add(coordY);
		coordinate.add(coordX);
		coordinates.add(coordinate); // Add the initial coordinate

		for (int i = 1 ; i < type.getLength() ; i++) {
			ArrayList<Integer> nextCoord = new ArrayList<Integer>();
			switch (direction) {
			case 'n': {
				coordY++;
				break;
			} case 's': {
				coordY--;
				break;
			} case 'e': {
				coordX++;
				break;
			} case 'w': {
				coordX--;
				break;
			}
			}

			nextCoord.add(coordY);
			nextCoord.add(coordX);
			coordinates.add(nextCoord); // 
		}

		return coordinates;
	}

	// Check if the ships are overlapping, if not generate a ship and put on map
	// Returns true if ship is successfully put.
	boolean putShipOnMap(ShipType type, int shipNumber, ArrayList<ArrayList<Integer>> coordinates) {

		boolean noOverlap = true;
		for (ArrayList<Integer> coordinate : coordinates) {
			if (SHIP_MAP[coordinate.get(0)][coordinate.get(1)] != null) {
				noOverlap = false;
			}
		}

		if (noOverlap) {
			Ship thisShip = new Ship(type, shipNumber);
			for (ArrayList<Integer> coordinate : coordinates) {
				HIT_MAP[coordinate.get(0)][coordinate.get(1)] = false;
				SHIP_MAP[coordinate.get(0)][coordinate.get(1)] = thisShip;
			}
		}

		return noOverlap;
	}

	// Pass a list of integers and check if they are all the same number
	boolean isAllTheSame(List<Integer> list) {
		int expectedValue = list.get(0);
		for (int value : list) {
			if (value != expectedValue) {
				return false;
			}
		}
		return true;
	}

	// Pass a list of integers and a maximum bound
	// check if they are all non-negative and below the maximum bound
	boolean isInbound(List<Integer> list, int minimum, int maximum) {
		for (int value : list) {
			if (value > maximum || value < minimum) {
				return false;
			}
		}
		return true;
	}

	// Pass a list of integers, sort and check if they are consecutive
	boolean isConsecutive(List<Integer> list) {
		Collections.sort(list);
		int expectedValue = list.get(0);
		for (int value : list) {
			if (value != expectedValue) {
				return false;
			}
			expectedValue++;
		}
		return true;
	}

	// Pass a ship type and its supposed coordinates, check if it is a valid ship
	boolean isValidShip (ShipType type, ArrayList<ArrayList<Integer>> coordinates) {

		List<Integer> coordX = new ArrayList<>();
		List<Integer> coordY = new ArrayList<>();

		for (ArrayList<Integer> coordinate : coordinates) {
			coordY.add(coordinate.get(0));
			coordX.add(coordinate.get(1));
		}

		if(isInbound(coordX, 0, BOARD_SIZE_X - 1) && isInbound(coordY, 0, BOARD_SIZE_Y - 1)) {
			if (isAllTheSame(coordX)) { // A vertical ship
				if (coordY.size() == type.getLength() && isConsecutive(coordY)) {
					return true;
				}
			}
			if (isAllTheSame(coordY)) { // A horizontal ship
				if (coordX.size() == type.getLength() && isConsecutive(coordX)) {
					return true;
				}
			}
		}

		return false;
	}

	public int getNumberOfShips() {
		return NO_OF_SHIPS;
	}

	public int getBoardSizeX() {
		return BOARD_SIZE_X;
	}

	public int getBoardSizeY() {
		return BOARD_SIZE_Y;
	}

	public Ship getShip(int coordX, int coordY) {
		return SHIP_MAP[coordY][coordX];
	}

	// Change the hitMap and call hit() of the ship object
	public void hit (int coordX, int coordY) {
		if (SHIP_MAP[coordY][coordX] != null) {
			HIT_MAP[coordY][coordX] = true;
			SHIP_MAP[coordY][coordX].hit();
		}
	}

	// Evaluate if all ships are sunk
	public boolean allHit() {
		boolean b = true;
		for (boolean[] rows : HIT_MAP) {
			for (boolean value : rows) {
				if (value == false) {
					b = false;
				}
			}
		}
		return b;
	}
}

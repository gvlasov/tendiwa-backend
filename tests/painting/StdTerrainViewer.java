package painting;

import java.util.HashSet;
import java.util.Set;

import tendiwa.core.Cell;
import tendiwa.core.Main;
import tendiwa.core.StaticData;
import tendiwa.core.TerrainBasics;
import tendiwa.recsys.RectangleArea;
import tendiwa.recsys.RectangleSystem;

public class StdTerrainViewer {
Set<RectangleSystem> rectangleSystems = new HashSet<RectangleSystem>();
/**
 * Width of the left ruler with numbers
 */
private int numbersWidth = 0;
/**
 * Width of the top ruler with numbers
 */
private int numbersHeight = 0;
private TerrainBasics terrain;
private int waterId = StaticData.getFloorType("water").getId();
private boolean printNumbersV = false;
private boolean printNumbersH = false;

public StdTerrainViewer(TerrainBasics terrain) {
	this.terrain = terrain;
	int d = 1;

	while (terrain.getWidth() > d) {
		d *= 10;
		numbersWidth++;
		numbersHeight++;
	}
}
public StdTerrainViewer printNumbers() {
	printNumbersVertical();
	return printNumbersHorizontal();
}
public StdTerrainViewer printNumbersHorizontal() {
	// -1 here is because, for example, terrain with with 100 will need only
	// 2 chars for digits, since it will need numbers from 0 to 99.
	numbersHeight = String.valueOf(terrain.getHeight() - 1).length();
	printNumbersH = true;
	return this;
}
public StdTerrainViewer printNumbersVertical() {
	// -1 here is because, for example, terrain with with 100 will need only
	// 2 chars for digits, since it will need numbers from 0 to 99.
	numbersWidth = String.valueOf(terrain.getWidth() - 1).length();
	printNumbersV = true;
	return this;
}
public StdTerrainViewer addRectangleSystem(RectangleSystem rs) {
	rectangleSystems.add(rs);
	return this;
}
public void print() {
	// These builders contain chars that are
	StringBuilder[] builders = new StringBuilder[terrain.getHeight()];
	int h = terrain.getHeight();
	Cell[][] cells = terrain.copyCells();
	for (int i = 0; i < h; i++) {
		StringBuilder builder = builders[i] = new StringBuilder();
		for (int j = 0, w = terrain.getWidth(); j < w; j++) {
			Cell cell = cells[j][i];
			if (cell.object() != StaticData.VOID) {
				if (terrain.isDoor(j, i)) {
					builder.append('+');
				} else {
					builder.append('8');
				}
			} else if (cell.character() != null) {
				builder.append('!');
			} else if (cell.floor() == waterId) {
				builder.append('~');
			} else {
				builder.append('.');
			}
		}
	}
	if (printNumbersV) {
		// Create numbers of lines from the left side of the terrain
		for (int i = 0; i < h; i++) {
			String number = new String();
			for (int j = 0, l = numbersWidth - String.valueOf(i).length(); j < l; j++) {
				number += " ";
			}
			number += i;
			builders[i].insert(0, number);
		}
	}
	StringBuilder[] hbuilders = null;
	if (printNumbersH) {
		// Create numbers of lines from the top side of terrain
		hbuilders = new StringBuilder[numbersHeight];
		for (int i = 0; i < numbersHeight; i++) {
			hbuilders[i] = new StringBuilder();
			if (printNumbersV) {
				// Move each string builder's contents to the right to fit
				// its actual x-coords. Add spaces in the beginning of each
				// StringBuilder
				hbuilders[i].append(new String(new char[numbersWidth]).replace("\0", " "));
			}
		}
		for (int i = 0, l = terrain.getWidth(); i < l; i += 2) {
			// Add each x-coord to the ruler as a vertical number whose
			// least significant digit is at the bottom and extra digits are
			// replaces with space chars.
			int number = i;
			for (int j = 0; j < numbersHeight; j++) {
				// Add each digit to builders (each digit to its own
				// builder, since builder represents a horizontal sequence
				// of characters).
				if (number != -1) {
					hbuilders[numbersHeight - j - 1].append(number % 10 + " ");
					number /= 10;
					if (number == 0) {
						// -1 is a special value indicating that the last
						// digit is reached. Though 0 is more natural, it
						// can't be used to signal that there are no more
						// digits, because 0 is a valid x-coord itself.
						number = -1;
					}
				} else {
					hbuilders[numbersHeight - j - 1].append(" " + " ");
				}
			}
		}
	}
	// Printing ids of rectangles
	for (RectangleSystem rs : rectangleSystems) {
		for (RectangleArea r : rs.rectangleSet()) {
			int numOfDigits = String.valueOf(r.getId()).length();
			if (r.width < numOfDigits + 1 || r.height < 2) {
				continue;
			}
			builders[r.y + 1].insert(numbersWidth + r.x + 1, r.getId());

			builders[r.y + 1].delete(numbersWidth + r.x + 1 + numOfDigits, numbersWidth + r.x + 1 + numOfDigits * 2);
		}
	}
	// Now finally printing the view of terrain.
	if (printNumbersH) {
		for (int i = 0; i < numbersHeight; i++) {
			System.out.println(hbuilders[i].toString());
		}
	}
	for (int i = 0; i < h; i++) {
		System.out.println(builders[i].toString());
	}
}
}

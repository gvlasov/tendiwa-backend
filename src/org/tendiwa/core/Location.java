package org.tendiwa.core;

import org.tendiwa.core.meta.Chance;
import org.tendiwa.core.settlements.BuildingPlace;
import org.tendiwa.geometry.*;

import java.awt.*;
import java.util.*;

/**
 * Location is a rectangle of cells lying over several {@link Chunk}s. API users write cell contents to Location calling
 * its methods, and that fills up the {@link HorizontalPlane} (by filling up Chunks) this Location is on.
 */
public class Location {
protected final int width;
protected final int height;
final int y;
final int x;
/**
 * A HorizontalPlane where entities are placed.
 */
private HorizontalPlane activePlane;

public Location(HorizontalPlane activePlane, int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.activePlane = activePlane;
	this.width = width;
	this.height = height;
}


public HorizontalPlane getActivePlane() {
	return activePlane;
}

/**
 * Changes active plane to another plane.
 *
 * @param height
 * 	Vertical number of plane. If plane with that number doesn't exist, it is created with a call to this method. Plane
 * 	{@code height-1} must exist to change to plane {@code height}.
 * @throws IllegalArgumentException
 * 	if there is no plane {@code height-1}.
 */
public void changePlane(int height) {
	activePlane = activePlane.getWorld().getPlane(height);
}

public void line(int startX, int startY, int endX, int endY, TypePlaceableInCell placeable) {
	if (startX == endX && startY == endY) {
		EntityPlacer.place(activePlane, placeable, x + startX, y + startY);
		return;
	}
	Cell[] cells = Chunk.vector(startX, startY, endX, endY);
	int size = cells.length;
	for (int i = 0; i < size - 1; i++) {
		int x = cells[i].getX();
		int y = cells[i].getY();
		int x2 = cells[i + 1].getX();
		int y2 = cells[i + 1].getY();

		EntityPlacer.place(activePlane, placeable, this.x + x, this.y + y);
		if (i < cells.length - 1 && x != x2 && y != y2) {
			int cx = x + ((x2 > x) ? 1 : -1);
			EntityPlacer.place(activePlane, placeable, this.x + cx, this.y + y);
		}
		if (i == size - 2) {
			EntityPlacer.place(activePlane, placeable, this.x + x2, this.y + y2);
		}
	}
}

public void square(int startX, int startY, int w, int h, TypePlaceableInCell placeable) {
	square(startX, startY, w, h, placeable, false);
}

public void square(EnhancedRectangle r, TypePlaceableInCell placeable, boolean fill) {
	square(r.getX(), r.getY(), r.getWidth(), r.getHeight(), placeable, fill);
}

public void square(int startX, int startY, int w, int h, TypePlaceableInCell placeable, boolean fill) {
	if (startX + w > getWidth() || startY + h > getHeight()) {
		throw new LocationException("Square " + startX + "," + startY + "," + w + "," + h + " goes out of borders of a " + getWidth() + "*" + getHeight() + " location");
	}
	if (w == 1) {
		line(startX, startY, startX, startY + h - 1, placeable);
	} else if (h == 1) {
		line(startX, startY, startX + w - 1, startY, placeable);
	} else {
		line(startX, startY, startX + w - 2, startY, placeable);
		line(startX, startY, startX, startY + h - 2, placeable);
		line(startX + w - 1, startY, startX + w - 1, startY + h - 1, placeable);
		line(startX, startY + h - 1, startX + w - 2, startY + h - 1, placeable);
		if (fill) {
			for (int i = 1; i < h - 1; i++) {
				line(startX + 1, startY + i, startX + w - 1, startY + i, placeable);
			}
		}
	}
}

public ArrayList<Cell> getCircle(int cx, int cy, int r) {
	ArrayList<Cell> answer = new ArrayList<>();
	int d = -r / 2;
	int xCoord = 0;
	int yCoord = r;
	HashMap<Integer, Integer> x = new HashMap<>();
	HashMap<Integer, Integer> y = new HashMap<>();
	x.put(0, 0);
	y.put(0, r);
	do {
		if (d < 0) {
			xCoord += 1;
			d += xCoord;
		} else {
			yCoord -= 1;
			d -= yCoord;
		}
		x.put(x.size(), xCoord);
		y.put(y.size(), yCoord);
	} while (yCoord > 0);
	int size = x.size();
	for (int i = 0; i < size; i++) {
		answer.add(new Cell(cx + x.get(i), cy + y.get(i)));
		answer.add(new Cell(cx - x.get(i), cy + y.get(i)));
		answer.add(new Cell(cx + x.get(i), cy - y.get(i)));
		answer.add(new Cell(cx - x.get(i), cy - y.get(i)));
	}
	return answer;
}

/**
 * Uses a {@link org.tendiwa.geometry.Segment} to drawWorld a rectangle. This method is almost identical to {@link Location#square(int, int,
 * int, int, TypePlaceableInCell)}, it is just more convenient to use when Segments are often used. The drawn
 * rectangle's top-left cell will be {segment.x;segment.y}.
 *
 * @param segment
 * 	A segment of cells to drawWorld.
 * @param width
 * 	Defines width (if segment.getDirection() == DirectionToBERemoved.V) of height (if segment.getDirection() ==
 * 	DirectionToBERemoved.H) of the drawn rectangle.
 * @param placeable
 * 	What to drawWorld in each cell
 */
public void drawSegment(Segment segment, int width, TypePlaceableInCell placeable) {
	if (segment.getOrientation().isHorizontal()) {
		square(segment.getX(), segment.getY(), segment.getLength(), width, placeable, true);
	} else {
		square(segment.getX(), segment.getY(), width, segment.getLength(), placeable, true);
	}
}

public void circle(int cX, int cY, int r, PlaceableInCell placeable) {
	circle(cX, cY, r, placeable, false);
}

public void circle(int cX, int cY, int r, PlaceableInCell placeable, boolean fill) {
	int d = -r / 2;
	int xCoord = 0;
	int yCoord = r;
	HashMap<Integer, Integer> x = new HashMap<>();
	HashMap<Integer, Integer> y = new HashMap<>();
	x.put(0, 0);
	y.put(0, r);
	do {
		if (d < 0) {
			xCoord += 1;
			d += xCoord;
		} else {
			yCoord -= 1;
			d -= yCoord;
		}
		x.put(x.size(), xCoord);
		y.put(y.size(), yCoord);
	} while (yCoord > 0);
	int size = x.size();
	for (int i = 0; i < size; i++) {
		placeable.place(activePlane, this.x + cX + x.get(i), this.y + cY + y.get(i));
		placeable.place(activePlane, this.x + cX - x.get(i), this.y + cY + y.get(i));
		placeable.place(activePlane, this.x + cX + x.get(i), this.y + cY - y.get(i));
		placeable.place(activePlane, this.x + cX - x.get(i), this.y + cY - y.get(i));
	}
}

public TerrainModifier getTerrainModifier(RectangleSystem rs) {
	return new TerrainModifier(this, rs);
}

public CellCollection getCellCollection(ArrayList<Cell> cls) {
	return new CellCollection(cls, this);
}

public <T extends Building> void placeBuilding(T building) {
	/**
	 * Places building when current location is not Settlement.
	 *
	 * @param side
	 *            What side a building is rotated to.
	 */
	BuildingPlace place = new BuildingPlace(x, y, width, height);
	building.draw();
}

// From TerrainGenerator
public ArrayList<Cell> polygon(ArrayList<Cell> coords) {
	return polygon(coords, false);
}

public ArrayList<Cell> polygon(ArrayList<Cell> coords, boolean mode) {
	ArrayList<Cell> answer = new ArrayList<>();

	int size = coords.size();
	Cell[] v;
	int vSize;
	for (int i = 0; i < size; i++) {
		Cell coord = coords.get(i);
		Cell nextCoord = coords.get((i == size - 1) ? 0 : i + 1);
		v = Chunk.vector(coord.getX(), coord.getY(), nextCoord.getX(), nextCoord.getY());
		vSize = v.length;
		for (int j = 0; j < vSize - 1; j++) {
			answer.add(v[j]);
		}
	}
	int startX = (int) Math.floor((coords.get(0).getX() + coords.get(1).getX() + coords.get(2).getX()) / 3);
	int startY = (int) Math.floor((coords.get(0).getY() + coords.get(1).getY() + coords.get(2).getY()) / 3);
	if (!mode) {
		HashSet<Cell> oldFront = new HashSet<>();
		HashSet<Cell> newFront = new HashSet<>();
		newFront.add(new Cell(startX, startY));
		int[][] pathTable = new int[getWidth()][getHeight()];
		for (int i = 0; i < getWidth(); i++) {
			Arrays.fill(pathTable[i], 0);
		}
		Iterator<Cell> it = answer.iterator();
		while (it.hasNext()) {
			Cell cell = it.next();
			pathTable[cell.getX()][cell.getY()] = 2;
		}
		answer = new ArrayList<>();
		do {
			oldFront = newFront;
			newFront = new HashSet<>();
			size = oldFront.size();
			it = oldFront.iterator();
			while (it.hasNext()) {
				Cell cell = it.next();
				int x = cell.getX();
				int y = cell.getY();
				int[] adjactentX = {x + 1, x, x, x - 1};
				int[] adjactentY = {y, y - 1, y + 1, y};
				for (int j = 0; j < 4; j++) {
					int thisNumX = adjactentX[j];
					int thisNumY = adjactentY[j];
					if (pathTable[thisNumX][thisNumY] != 0 && pathTable[thisNumX][thisNumY] != 2) {
						continue;
					}
					if (thisNumX < 0 || thisNumX >= getWidth() || thisNumY < 0 || thisNumY >= getHeight()) {
						continue;
					}
					// if (thisNumX<=0 || thisNumX>=w-1 || thisNumY<=0 ||
					// thisNumY>=h-1) {
					// // ��������, ����� ��� ��������� �������� ������ ��
					// �������� �� �������
					// continue;
					// }
					if (pathTable[thisNumX][thisNumY] == 0) {
						newFront.add(new Cell(thisNumX, thisNumY));
					}
					answer.add(new Cell(thisNumX, thisNumY));
					pathTable[thisNumX][thisNumY] = 1;
				}
			}
		} while (newFront.size() > 0);
	}
	return answer;
}

public void fillWithCells(FloorType floor) {
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < height; j++) {
			activePlane.placeFloor(floor, x + i, y + j);
		}
	}
}

public ArrayList<Cell> closeCells(int startX, int startY, int length, Passability pass, boolean noDiagonal) {
	ArrayList<Cell> oldFront = new ArrayList<>();
	ArrayList<Cell> newFront = new ArrayList<>();
	ArrayList<Cell> answer = new ArrayList<>();
	answer.add(new Cell(startX, startY));
	newFront.add(new Cell(startX, startY));
	int[][] pathTable = new int[width][height];
	for (int i = 0; i < width; i++) {
		Arrays.fill(pathTable[i], 0);
	}
	int numOfSides = noDiagonal ? 4 : 8;
	int[] adjactentX;
	int[] adjactentY;
	if (noDiagonal) {
		adjactentX = new int[]{0, 1, 0, -1};
		adjactentY = new int[]{-1, 0, 1, 0};
	} else {
		adjactentX = new int[]{0, 1, 0, -1, 1, 1, -1, -1};
		adjactentY = new int[]{-1, 0, 1, 0, 1, -1, 1, -1};
	}
	do {
		oldFront = newFront;
		newFront = new ArrayList<Cell>();
		Iterator<Cell> it = oldFront.iterator();
		while (it.hasNext()) {
			// ������� ����� �� ������ ��������� ������ �� ������ ������
			Cell c = it.next();
			int x = c.getX();
			int y = c.getY();

			for (int j = 0; j < numOfSides; j++) {
				int thisNumX = x + adjactentX[j];
				int thisNumY = y + adjactentY[j];
				if (thisNumX <= 0 || thisNumX >= getWidth() - 1 || thisNumY <= 0 || thisNumY >= getHeight() - 1) {
					// ��������, ����� ��� ��������� �������� ������ ��
					// �������� �� �������
					continue;
				}
				// if (thisNumX < 0 || thisNumX >= width || thisNumY < 0
				// || thisNumY >= height) {
				// // �� ������� ������ �� �������, ������� ������� ��
				// // ������� ���� ��� �������� ��� ���������
				// continue;
				// }
				if (pathTable[thisNumX][thisNumY] != 0) {
					continue;
				}

				if (activePlane.getPassability(thisNumX, thisNumY) != pass) {
					continue;
				}
				if (Math.floor(Cells.distanceInt(startX, startY, thisNumX, thisNumY)) >= length) {
					continue;
				}
				newFront.add(new Cell(thisNumX, thisNumY));
				answer.add(new Cell(thisNumX, thisNumY));
				pathTable[thisNumX][thisNumY] = 1;
			}
		}
	} while (newFront.size() > 0);
	return answer;
}

public ArrayList<Cell> getElementsAreaBorder(int startX, int startY, PlaceableInCell placeable, int depth, boolean noDiagonal) {
	// �������� ������� ������� � ���������� ���� %ammunitionType% ���� %val%, �������
	// �� ����� ��� � %depth% ������� �� ��������� ������
	// noDiagonal - �������� ������� ���������� ������ �� ������ �������,
	// ��� �� ��� ������ ������.
	int[][] pathTable = new int[getWidth()][getHeight()];
	ArrayList<Cell> cells = new ArrayList<>();
	ArrayList<Cell> oldFront = new ArrayList<>();
	ArrayList<Cell> newFront = new ArrayList<>();
	// �� ����� ������ �������� ������
	newFront.add(new Cell(startX, startY));
	for (int i = 0; i < getWidth(); i++) {
		for (int j = 0; j < getHeight(); j++) {
			pathTable[i][j] = 0;
		}
	}
	pathTable[startX][startY] = 0;
	int t = 0;
	int numOfSides = noDiagonal ? 4 : 8;
	int[] adjactentX;
	int[] adjactentY;
	if (noDiagonal) {
		adjactentX = new int[]{0, 1, 0, -1};
		adjactentY = new int[]{-1, 0, 1, 0};
	} else {
		adjactentX = new int[]{0, 1, 0, -1, 1, 1, -1, -1};
		adjactentY = new int[]{-1, 0, 1, 0, 1, -1, 1, -1};
	}
	do {
		oldFront = newFront;
		newFront = new ArrayList<>();
		for (int i = 0; i < oldFront.size(); i++) {
			int x = oldFront.get(i).getX();
			int y = oldFront.get(i).getY();
			for (int j = 0; j < numOfSides; j++) {
				int thisNumX = x + adjactentX[j];
				int thisNumY = y + adjactentY[j];
				if (thisNumX < 0 || thisNumX >= getWidth() || thisNumY < 0 || thisNumY >= getHeight() || pathTable[thisNumX][thisNumY] != 0 || Cells.distanceInt(startX, startY, thisNumX, thisNumY) > depth) {
					continue;
				}
				if (placeable.containedIn(activePlane, thisNumX, thisNumY) && !(thisNumX == startX && thisNumY == startY)) {
					pathTable[thisNumX][thisNumY] = t + 1;
					newFront.add(new Cell(thisNumX, thisNumY));
				} else if (!placeable.containedIn(activePlane, thisNumX, thisNumY)) {
					cells.add(new Cell(x, y));
				}
			}
		}
		t++;
	} while (newFront.size() > 0);
	return cells;
}

public void waveStructure(int startX, int startY, PlaceableInCell placeable, int maxSize) {
	Hashtable<Integer, Cell> newFront = new Hashtable<>();
	newFront.put(0, new Cell(startX, startY));
	int[][] canceled = new int[getWidth()][getHeight()];
	int[][] pathTable = new int[getWidth()][getHeight()];
	for (int i = 0; i < getWidth(); i++) {
		Arrays.fill(pathTable[i], 0);
		Arrays.fill(canceled[i], 0);
	}
	placeable.place(activePlane, startX, startY);
	int t = 0;
	do {
		int size = newFront.size();
		for (int i = 0; i < size; i++) {
			Cell c = newFront.get(i);
			int x = c.getX();
			int y = c.getY();
			int[] adjactentX = {x + 1, x, x, x - 1};
			int[] adjactentY = {y, y - 1, y + 1, y};
			for (int j = 0; j < 4; j++) {
				int thisNumX = adjactentX[j];
				int thisNumY = adjactentY[j];
				if (thisNumX < 0 || thisNumX >= getWidth() || thisNumY < 0 || thisNumY >= getHeight() || canceled[thisNumX][thisNumY] != 0) {
					continue;
				}
				if (thisNumX <= 0 || thisNumX >= getWidth() - 1 || thisNumY <= 0 || thisNumY >= getHeight() - 1) {
					continue;
				}
				// TODO: This has been making compile time errors so I commented it out : (
//				if (getElement(thisNumX + 1, thisNumY, ammunitionType) + getElement(thisNumX - 1, thisNumY, ammunitionType) + getElement(thisNumX, thisNumY + 1, ammunitionType) + getElement(thisNumX, thisNumY - 1, ammunitionType) + getElement(thisNumX + 1, thisNumY + 1, ammunitionType) + getElement(thisNumX - 1, thisNumY + 1, ammunitionType) + getElement(thisNumX + 1, thisNumY - 1, ammunitionType) + getElement(thisNumX - 1, thisNumY - 1, ammunitionType) > 3 && t > 4) {
//					continue;
//				}
				Chance chance = new Chance(15);
				if (chance.roll()) {
					canceled[thisNumX][thisNumY] = 1;
					continue;
				}
				placeable.place(activePlane, thisNumX, thisNumY);
				newFront.put(newFront.size(), new Cell(thisNumX, thisNumY));
			}
		}
		t++;
	} while (newFront.size() > 0 && t < maxSize);
}

public CellCollection newCellCollection(Collection<Cell> cls) {
	return new CellCollection(cls, this);
}

public int[][] getPathTable(int startX, int startY, int endX, int endY, boolean noDiagonal) {
	int[][] pathTable = new int[getWidth()][getHeight()];
	boolean isPathFound = false;
	ArrayList<Cell> oldFront = new ArrayList<>();
	ArrayList<Cell> newFront = new ArrayList<>();
	newFront.add(new Cell(startX, startY));
	for (int i = 0; i < getWidth(); i++) {
		for (int j = 0; j < getHeight(); j++) {
			pathTable[i][j] = 0;
		}
	}
	pathTable[startX][startY] = 0;
	int t = 0;
	int numOfSides = noDiagonal ? 4 : 8;
	int[] adjactentX;
	int[] adjactentY;
	if (noDiagonal) {
		adjactentX = new int[]{0, 1, 0, -1};
		adjactentY = new int[]{-1, 0, 1, 0};
	} else {
		adjactentX = new int[]{0, 1, 0, -1, 1, 1, -1, -1};
		adjactentY = new int[]{-1, 0, 1, 0, 1, -1, 1, -1};
	}
	do {
		oldFront = newFront;
		newFront = new ArrayList<Cell>();
		for (int i = 0; i < oldFront.size(); i++) {
			int x = oldFront.get(i).getX();
			int y = oldFront.get(i).getY();
			for (int j = 0; j < numOfSides; j++) {
				int thisNumX = x + adjactentX[j];
				int thisNumY = y + adjactentY[j];
				if (thisNumX < 0 || thisNumX >= getWidth() || thisNumY < 0 || thisNumY >= getHeight() || pathTable[thisNumX][thisNumY] != 0) {
					continue;
				}
				if (thisNumX == endX && thisNumY == endY) {
					isPathFound = true;
				}
				if (activePlane.getPassability(thisNumX, thisNumY) == Passability.FREE && !(thisNumX == startX && thisNumY == startY)) {
					pathTable[thisNumX][thisNumY] = t + 1;
					newFront.add(new Cell(thisNumX, thisNumY));
				}
			}
		}
		t++;
	} while (newFront.size() > 0 && !isPathFound && t < 1000);
	return pathTable;
}

public ArrayList<Cell> getPath(int startX, int startY, int destinationX, int destinationY, boolean noDiagonal) {
	// �������� ���� �� ������ � ���� ������� ��������� (0 - ������ ��� � �.
	// �.)
	if (destinationX == startX && destinationY == startY) {
		throw new Error("Getting path to itself");
	}
	int[][] pathTable = getPathTable(startX, startY, destinationX, destinationY, noDiagonal);
	ArrayList<Cell> path = new ArrayList<Cell>();
	if (Cells.isNear(startX, startY, destinationX, destinationY)) {
		path.add(new Cell(destinationX, destinationY));
		return path;
	}
	// ���������� ����
	path.add(new Cell(startX, startY));
	int currentNumX = destinationX;
	int currentNumY = destinationY;
	int x = currentNumX;
	int y = currentNumY;
	int numOfSides = noDiagonal ? 4 : 8;
	int[] adjactentX;
	int[] adjactentY;
	if (noDiagonal) {
		adjactentX = new int[]{0, 1, 0, -1};
		adjactentY = new int[]{-1, 0, 1, 0};
	} else {
		adjactentX = new int[]{0, 1, 0, -1, 1, 1, -1, -1};
		adjactentY = new int[]{-1, 0, 1, 0, 1, -1, 1, -1};
	}
	for (int j = pathTable[currentNumX][currentNumY]; j > 0; j = pathTable[currentNumX][currentNumY]) {
		// �������: �� ���-�� ����� �� ������ dest �� ��������� ������ (���
		// 1)
		path.add(0, new Cell(currentNumX, currentNumY));
		currentNumX = -1;
		for (int i = 0; i < numOfSides; i++) {
			// ��� ������ �� ��������� ������ (�, �, �, �)
			int thisNumX = x + adjactentX[i];
			if (thisNumX < 0 || thisNumX >= getWidth()) {
				continue;
			}
			int thisNumY = y + adjactentY[i];
			if (thisNumY < 0 || thisNumY >= getHeight()) {
				continue;
			}
			if (pathTable[thisNumX][thisNumY] == j - 1 && (currentNumX == -1 || Cells.distanceInt(thisNumX, thisNumY, destinationX, destinationY) < Cells.distanceInt(currentNumX, currentNumY, destinationX, destinationY))) {
				// ���� ������ � ���� ������� �������� ���������� �����,
				// ������� �� ��
				currentNumX = thisNumX;
				currentNumY = thisNumY;
			}
		}
		x = currentNumX;
		y = currentNumY;
	}
	return path;
}

//protected void cellularAutomataSmooth(int level, int ammunitionType, PlaceableInCell formerContent, PlaceableInCell newContent) {
//	// Smooth the borders of terrain's areas consisting of
//	// elements with %ammunitionType% and %val%
//	for (int l = 0; l < level; l++) {
//		Cell[][] bufCells = new Cell[getWidth()][getHeight()];
//		for (int i = 0; i < getHeight(); i++) {
//			for (int j = 0; j < getWidth(); j++) {
//				bufCells[j][i] = new Cell(cells[j][i]);
//			}
//		}
//		for (int i = 0; i < getWidth(); i++) {
//			for (int j = 0; j < getHeight(); j++) {
//				int count = 0;
//				boolean iGT0 = i > 0;
//				boolean iLTw = i < getWidth() - 1;
//				boolean jGT0 = j > 0;
//				boolean jLTh = j < getHeight() - 1;
//				if (jGT0 && bufCells[i][j - 1].contains(formerContent)) {
//					count++;
//				}
//				if (iLTw && jGT0 && bufCells[i + 1][j - 1].contains(formerContent)) {
//					count++;
//				}
//				if (iLTw && bufCells[i + 1][j].contains(formerContent)) {
//					count++;
//				}
//				if (iLTw && jLTh && bufCells[i + 1][j + 1].contains(formerContent)) {
//					count++;
//				}
//				if (jLTh && bufCells[i][j + 1].contains(formerContent)) {
//					count++;
//				}
//				if (iGT0 && jLTh && bufCells[i - 1][j + 1].contains(formerContent)) {
//					count++;
//				}
//				if (iGT0 && bufCells[i - 1][j].contains(formerContent)) {
//					count++;
//				}
//				if (iGT0 && jGT0 && bufCells[i - 1][j - 1].contains(formerContent)) {
//					count++;
//				}
//
//				if (bufCells[i][j].contains(formerContent) && count > 4) {
//					setElement(i, j, formerContent);
//				} else if (bufCells[i][j].contains(formerContent) && count < 4) {
//					setElement(i, j, newContent);
//				}
//			}
//		}
//	}
//}

/**
 * Default bold line with width of 3 cells
 *
 * @param startX
 * @param startY
 * @param endX
 * @param endY
 * @param placeable
 */
public void boldLine(int startX, int startY, int endX, int endY, TypePlaceableInCell placeable) {
	boldLine(startX, startY, endX, endY, placeable, 3);
}

public void boldLine(int startX, int startY, int endX, int endY, TypePlaceableInCell placeable, int w) {
	int dx;
	int dy;
	if (endX - startX == 0) {
		dx = 1;
		dy = 0;
	} else {
		int tg = (endY - startY) / (endX - startX);
		if (tg > -0.5 && tg < 0.5) {
			dx = 0;
			dy = 1;
		} else {
			dx = 1;
			dy = 0;
		}
	}
	int coeff = (int) Math.floor(w / 2);
	startX -= dx * coeff;
	startY -= dy * coeff;
	endX -= dx * coeff;
	endY -= dy * coeff;
	if (startX < 0) {
		startX = 0;
	} else if (startX >= getWidth()) {
		startX = getWidth() - 1;
	}
	if (startY < 0) {
		startY = 0;
	} else if (startY >= getHeight()) {
		startY = getHeight() - 1;
	}
	if (endX < 0) {
		endX = 0;
	} else if (endX >= getWidth()) {
		endX = getWidth();
	}
	if (endY < 0) {
		endY = 0;
	} else if (endY >= getHeight()) {
		endY = getHeight() - 1;
	}
	for (int i = 0; i < w; i++) {
//		line(startX, startY, endX, endY, placeable);
		startX += dx;
		startY += dy;
		endX += dx;
		endY += dy;
	}
}

public void drawPath(int startX, int startY, int endX, int endY, PlaceableInCell placeable) {
	ArrayList<Cell> path = getPath(startX, startY, endX, endY, true);
	int size = path.size();
	for (int i = 0; i < size; i++) {
		Cell coordinate = path.get(i);
		placeable.place(activePlane, coordinate.getX(), coordinate.getY());
	}
}

protected CellCollection getCoast(int startX, int startY) {
	int[][] pathTable = new int[getWidth()][getHeight()];
	ArrayList<Cell> cells = new ArrayList<Cell>();
	ArrayList<Cell> oldFront = new ArrayList<Cell>();
	ArrayList<Cell> newFront = new ArrayList<>();
	newFront.add(new Cell(startX, startY));
	for (int i = 0; i < getWidth(); i++) {
		for (int j = 0; j < getHeight(); j++) {
			pathTable[i][j] = 0;
		}
	}
	pathTable[startX][startY] = 0;
	int t = 0;
	do {
		oldFront = newFront;
		newFront = new ArrayList<>();
		for (int i = 0; i < oldFront.size(); i++) {
			int x = oldFront.get(i).getX();
			int y = oldFront.get(i).getY();
			int[] adjactentX = new int[]{x + 1, x, x, x - 1,};
			int[] adjactentY = new int[]{y, y - 1, y + 1, y};
			for (int j = 0; j < 4; j++) {
				int thisNumX = adjactentX[j];
				int thisNumY = adjactentY[j];
				if (thisNumX < 0 || thisNumX >= getWidth() || thisNumY < 0 || thisNumY >= getHeight() || pathTable[thisNumX][thisNumY] != 0) {
					continue;
				}
				if (activePlane.getPassability(thisNumX, thisNumY) == Passability.NO && !(thisNumX == startX && thisNumY == startY)) {
					pathTable[thisNumX][thisNumY] = t + 1;
					newFront.add(new Cell(thisNumX, thisNumY));
				} else if (activePlane.getPassability(thisNumX, thisNumY) != Passability.NO) {
					cells.add(new Cell(x, y));
				}
			}
		}
		t++;
	} while (newFront.size() > 0 && t < 2000);
	return newCellCollection(cells);
}

public ArrayList<Cell> getCellsAroundCell(int x, int y) {
	ArrayList<Cell> answer = new ArrayList<Cell>();
	int x1[] = {x, x + 1, x + 1, x + 1, x, x - 1, x - 1, x - 1};
	int y1[] = {y - 1, y - 1, y, y + 1, y + 1, y + 1, y, y - 1};
	for (int i = 0; i < 8; i++) {
		if (activePlane.getPassability(x1[i], y1[i]) == Passability.FREE) {
			answer.add(new Cell(x1[i], y1[i]));
		}
	}
	return answer;
}

public void lineToRectangleBorder(int startX, int startY, CardinalDirection side, Rectangle r, TypePlaceableInCell placeable) {
	if (!r.contains(startX, startY)) {
		throw new Error("Rectangle " + r + " contains no point " + startX + ":" + startY);
	}
	if (side == null) {
		throw new NullPointerException();
	}
	int endX, endY;
	switch (side) {
		case N:
			endX = startX;
			endY = r.y;
			break;
		case E:
			endX = r.x + r.width - 1;
			endY = startY;
			break;
		case S:
			endX = startX;
			endY = r.y + r.height - 1;
			break;
		case W:
		default:
			endX = r.x;
			endY = startY;
	}
	line(startX, startY, endX, endY, placeable);
}

public void fillSideOfRectangle(Rectangle r, CardinalDirection side, TypePlaceableInCell placeable) {
	int startX, startY, endX, endY;
	switch (side) {
		case N:
			startX = r.x;
			startY = r.y;
			endX = r.x + r.width - 1;
			endY = r.y;
			break;
		case E:
			startX = r.x + r.width - 1;
			startY = r.y;
			endX = r.x + r.width - 1;
			endY = r.y + r.height - 1;
			break;
		case S:
			startX = r.x;
			startY = r.y + r.height - 1;
			endX = r.x + r.width - 1;
			endY = r.y + r.height - 1;
			break;
		case W:
			startX = r.x;
			startY = r.y;
			endX = r.x;
			endY = r.y + r.height - 1;
			break;
		default:
			throw new Error("Incorrect side " + side);
	}
	line(startX, startY, endX, endY, placeable);
}

/**
 * Fills a rectange area in location coordinates with some placeable entities.
 *
 * @param r
 * 	A rectangle area in location coordinates, i.e. {0:0} is location's top left corner, not world's.
 * @param placeable
 * 	What to fill the rectanlge area with.
 */
public void fillRectangle(Rectangle r, TypePlaceableInCell placeable) {
	/**
	 * Fill rectngle with objects randomly. chance% of cells will be filled
	 * with these objects.
	 */
	int x = 0;
	int y = 0;
	try {
		for (x = r.x; x < r.x + r.width; x++) {
			for (y = r.y; y < r.y + r.height; y++) {
				EntityPlacer.place(activePlane, placeable, this.x + x, this.y + y);
			}
		}
	} catch (IndexOutOfBoundsException e) {
		throw new LocationException("Trying to place entity " + placeable + " outside of location at cell " + x + ":" + y);
	}
}

public int getWidth() {
	return width;
}

public int getHeight() {
	return height;
}

public TerrainTransition.TerrainTransitionBuilder transitionBuilder() {
	return new TerrainTransition.TerrainTransitionBuilder().setLocation(this);
}

/**
 * Places a single entity in a cell.
 *
 * @param placeable
 * 	What to place in a cell.
 * @param x
 * 	X coordinate of a cell in location coordinates.
 * @param y
 * 	Y coordinate of a cell in location coordinates.
 */
public void place(TypePlaceableInCell placeable, int x, int y) {
	assert placeable != null;
	EntityPlacer.place(activePlane, placeable, this.x + x, this.y + y);
}

/**
 * Places a single entity in a cell.
 *
 * @param placeable
 * 	What to place in a cell.
 * @param point
 * 	Point in location coordinates.
 */
public void place(TypePlaceableInCell placeable, Cell point) {
	assert placeable != null : "Trying to place a null object";
	assert point != null;
	EntityPlacer.place(activePlane, placeable, x + point.getX(), y + point.getY());
}

public void lineOfThin(RectangleSidePiece line, BorderObjectType type) {
	for (Cell point : line.getSegment()) {
		activePlane.setBorderObject(point.getX(), point.getY(), line.getDirection(), type);
	}
}
public void squareOfThin(EnhancedRectangle r, BorderObjectType type) {
	for (int i = 0; i < r.getWidth(); i++) {
		activePlane.setBorderObject(r.getX() + i, r.getY(), Directions.N, type);
		activePlane.setBorderObject(r.getX() + i, r.getY() + r.getHeight() - 1, Directions.S, type);
	}
	for (int i = 0; i < r.getHeight(); i++) {
		activePlane.setBorderObject(r.getX(), r.getY() + i, Directions.W, type);
		activePlane.setBorderObject(r.getX() + r.getWidth() - 1, r.getY() + i, Directions.E, type);
	}
}
}

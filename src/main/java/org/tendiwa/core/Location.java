package org.tendiwa.core;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.geometry.*;

import java.util.*;

/**
 * A modifiable view of a portion of a {@link org.tendiwa.core.HorizontalPlane}.
 */
public class Location {
	private final Rectangle rectangle;
	/**
	 * A HorizontalPlane where entities are placed.
	 */
	private HorizontalPlane activePlane;

	public Location(HorizontalPlane activePlane, Rectangle rectangle) {
		this.rectangle = rectangle;
		this.activePlane = activePlane;
	}

	public HorizontalPlane getActivePlane() {
		return activePlane;
	}

	/**
	 * Changes active plane to another plane.
	 *
	 * @param height
	 * 	Vertical number of plane. If plane with that number doesn't exist, it is created with a call to this method.
	 * 	Plane
	 * 	{@code height-1} must exist to change to plane {@code height}.
	 * @throws IllegalArgumentException
	 * 	if there is no plane {@code height-1}.
	 */
	public void changePlane(int height) {
		activePlane = activePlane.getWorld().getPlane(height);
	}

	public void line(int startX, int startY, int endX, int endY, TypePlaceableInCell placeable) {
		if (startX == endX && startY == endY) {
			EntityPlacer.place(activePlane, placeable, rectangle.x + startX, rectangle.y + startY);
			return;
		}
		BasicCell[] cells = CellSegment.cells(startX, startY, endX, endY);
		int size = cells.length;
		for (int i = 0; i < size - 1; i++) {
			int x = cells[i].x();
			int y = cells[i].y();
			int x2 = cells[i + 1].x();
			int y2 = cells[i + 1].y();

			EntityPlacer.place(activePlane, placeable, rectangle.x + x, rectangle.y + y);
			if (i < cells.length - 1 && x != x2 && y != y2) {
				int cx = x + ((x2 > x) ? 1 : -1);
				EntityPlacer.place(activePlane, placeable, rectangle.x + cx, rectangle.y + y);
			}
			if (i == size - 2) {
				EntityPlacer.place(activePlane, placeable, rectangle.x + x2, rectangle.y + y2);
			}
		}
	}

	public void square(int startX, int startY, int w, int h, TypePlaceableInCell placeable) {
		square(startX, startY, w, h, placeable, false);
	}

	public void square(Rectangle r, TypePlaceableInCell placeable, boolean fill) {
		square(r.getX(), r.getY(), r.width(), r.height(), placeable, fill);
	}

	public void square(int startX, int startY, int w, int h, TypePlaceableInCell placeable, boolean fill) {
		if (startX + w > rectangle.width || startY + h > rectangle.height) {
			throw new LocationException("Square " + startX + "," + startY + "," + w + "," + h + " goes out of borders of a " + rectangle.width + "*" + rectangle.height + " location");
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

	public ArrayList<BasicCell> getCircle(int cx, int cy, int r) {
		ArrayList<BasicCell> answer = new ArrayList<>();
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
			answer.add(new BasicCell(cx + x.get(i), cy + y.get(i)));
			answer.add(new BasicCell(cx - x.get(i), cy + y.get(i)));
			answer.add(new BasicCell(cx + x.get(i), cy - y.get(i)));
			answer.add(new BasicCell(cx - x.get(i), cy - y.get(i)));
		}
		return answer;
	}

	/**
	 * Uses a {@link org.tendiwa.geometry.BasicOrthoCellSegment} to drawWorld a rectangle. This method is almost identical to {@link
	 * Location#square(int, int,
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
	public void drawSegment(OrthoCellSegment segment, int width, TypePlaceableInCell placeable) {
		if (segment.orientation().isHorizontal()) {
			square(segment.getX(), segment.getY(), segment.length(), width, placeable, true);
		} else {
			square(segment.getX(), segment.getY(), width, segment.length(), placeable, true);
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
			placeable.place(activePlane, rectangle.x + cX + x.get(i), rectangle.y + cY + y.get(i));
			placeable.place(activePlane, rectangle.x + cX - x.get(i), rectangle.y + cY + y.get(i));
			placeable.place(activePlane, rectangle.x + cX + x.get(i), rectangle.y + cY - y.get(i));
			placeable.place(activePlane, rectangle.x + cX - x.get(i), rectangle.y + cY - y.get(i));
		}
	}

	public TerrainModifier getTerrainModifier(RectangleSystem rs) {
		return new TerrainModifier(this, rs);
	}

	public CellCollection getCellCollection(ArrayList<BasicCell> cls) {
		return new CellCollection(cls, this);
	}

	// From TerrainGenerator
	public ArrayList<BasicCell> polygon(ArrayList<BasicCell> coords) {
		return polygon(coords, false);
	}

	public ArrayList<BasicCell> polygon(ArrayList<BasicCell> coords, boolean mode) {
		ArrayList<BasicCell> answer = new ArrayList<>();

		int size = coords.size();
		BasicCell[] v;
		int vSize;
		for (int i = 0; i < size; i++) {
			BasicCell coord = coords.get(i);
			BasicCell nextCoord = coords.get((i == size - 1) ? 0 : i + 1);
			v = CellSegment.cells(coord.x(), coord.y(), nextCoord.x(), nextCoord.y());
			vSize = v.length;
			for (int j = 0; j < vSize - 1; j++) {
				answer.add(v[j]);
			}
		}
		int startX = (int) Math.floor((coords.get(0).x() + coords.get(1).x() + coords.get(2).x()) / 3);
		int startY = (int) Math.floor((coords.get(0).y() + coords.get(1).y() + coords.get(2).y()) / 3);
		if (!mode) {
			HashSet<BasicCell> oldFront = new HashSet<>();
			HashSet<BasicCell> newFront = new HashSet<>();
			newFront.add(new BasicCell(startX, startY));
			int[][] pathTable = new int[rectangle.width][rectangle.height];
			for (int i = 0; i < rectangle.width; i++) {
				Arrays.fill(pathTable[i], 0);
			}
			Iterator<BasicCell> it = answer.iterator();
			while (it.hasNext()) {
				BasicCell cell = it.next();
				pathTable[cell.x()][cell.y()] = 2;
			}
			answer = new ArrayList<>();
			do {
				oldFront = newFront;
				newFront = new HashSet<>();
				size = oldFront.size();
				it = oldFront.iterator();
				while (it.hasNext()) {
					BasicCell cell = it.next();
					int x = cell.x();
					int y = cell.y();
					int[] adjactentX = {x + 1, x, x, x - 1};
					int[] adjactentY = {y, y - 1, y + 1, y};
					for (int j = 0; j < 4; j++) {
						int thisNumX = adjactentX[j];
						int thisNumY = adjactentY[j];
						if (pathTable[thisNumX][thisNumY] != 0 && pathTable[thisNumX][thisNumY] != 2) {
							continue;
						}
						if (thisNumX < 0 || thisNumX >= rectangle.width || thisNumY < 0 || thisNumY >= rectangle.height) {
							continue;
						}
						// if (thisNumX<=0 || thisNumX>=w-1 || thisNumY<=0 ||
						// thisNumY>=h-1) {
						// // ��������, ����� ��� ��������� �������� ������ ��
						// �������� �� �������
						// continue;
						// }
						if (pathTable[thisNumX][thisNumY] == 0) {
							newFront.add(new BasicCell(thisNumX, thisNumY));
						}
						answer.add(new BasicCell(thisNumX, thisNumY));
						pathTable[thisNumX][thisNumY] = 1;
					}
				}
			} while (newFront.size() > 0);
		}
		return answer;
	}

	public void fillWithCells(FloorType floor) {
		for (int i = 0; i < rectangle.width; i++) {
			for (int j = 0; j < rectangle.height; j++) {
				activePlane.placeFloor(floor, rectangle.x + i, rectangle.y + j);
			}
		}
	}

	public ArrayList<BasicCell> closeCells(int startX, int startY, int length, Passability pass, boolean noDiagonal) {
		ArrayList<BasicCell> oldFront = new ArrayList<>();
		ArrayList<BasicCell> newFront = new ArrayList<>();
		ArrayList<BasicCell> answer = new ArrayList<>();
		answer.add(new BasicCell(startX, startY));
		newFront.add(new BasicCell(startX, startY));
		int[][] pathTable = new int[rectangle.width][rectangle.height];
		for (int i = 0; i < rectangle.width; i++) {
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
			newFront = new ArrayList<BasicCell>();
			Iterator<BasicCell> it = oldFront.iterator();
			while (it.hasNext()) {
				// ������� ����� �� ������ ��������� ������ �� ������ ������
				BasicCell c = it.next();
				int x = c.x();
				int y = c.y();

				for (int j = 0; j < numOfSides; j++) {
					int thisNumX = x + adjactentX[j];
					int thisNumY = y + adjactentY[j];
					if (thisNumX <= 0 || thisNumX >= rectangle.width - 1 || thisNumY <= 0 || thisNumY >= rectangle.height - 1) {
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
					newFront.add(new BasicCell(thisNumX, thisNumY));
					answer.add(new BasicCell(thisNumX, thisNumY));
					pathTable[thisNumX][thisNumY] = 1;
				}
			}
		} while (newFront.size() > 0);
		return answer;
	}

	public ArrayList<BasicCell> getElementsAreaBorder(
		int startX,
		int startY,
		PlaceableInCell placeable,
		int depth,
		boolean noDiagonal
	) {
		// �������� ������� ������� � ���������� ���� %ammunitionType% ���� %val%, �������
		// �� ����� ��� � %depth% ������� �� ��������� ������
		// noDiagonal - �������� ������� ���������� ������ �� ������ �������,
		// ��� �� ��� ������ ������.
		int[][] pathTable = new int[rectangle.width][rectangle.height];
		ArrayList<BasicCell> cells = new ArrayList<>();
		ArrayList<BasicCell> oldFront = new ArrayList<>();
		ArrayList<BasicCell> newFront = new ArrayList<>();
		// �� ����� ������ �������� ������
		newFront.add(new BasicCell(startX, startY));
		for (int i = 0; i < rectangle.width; i++) {
			for (int j = 0; j < rectangle.height; j++) {
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
				int x = oldFront.get(i).x();
				int y = oldFront.get(i).y();
				for (int j = 0; j < numOfSides; j++) {
					int thisNumX = x + adjactentX[j];
					int thisNumY = y + adjactentY[j];
					if (thisNumX < 0 || thisNumX >= rectangle.width || thisNumY < 0 || thisNumY >= rectangle.height || pathTable[thisNumX][thisNumY] != 0 || Cells.distanceInt(startX, startY, thisNumX, thisNumY) > depth) {
						continue;
					}
					if (placeable.containedIn(activePlane, thisNumX, thisNumY) && !(thisNumX == startX && thisNumY == startY)) {
						pathTable[thisNumX][thisNumY] = t + 1;
						newFront.add(new BasicCell(thisNumX, thisNumY));
					} else if (!placeable.containedIn(activePlane, thisNumX, thisNumY)) {
						cells.add(new BasicCell(x, y));
					}
				}
			}
			t++;
		} while (newFront.size() > 0);
		return cells;
	}

	public void waveStructure(int startX, int startY, PlaceableInCell placeable, int maxSize) {
		Hashtable<Integer, BasicCell> newFront = new Hashtable<>();
		newFront.put(0, new BasicCell(startX, startY));
		int[][] canceled = new int[rectangle.width][rectangle.height];
		int[][] pathTable = new int[rectangle.width][rectangle.height];
		for (int i = 0; i < rectangle.width; i++) {
			Arrays.fill(pathTable[i], 0);
			Arrays.fill(canceled[i], 0);
		}
		placeable.place(activePlane, startX, startY);
		int t = 0;
		do {
			int size = newFront.size();
			for (int i = 0; i < size; i++) {
				BasicCell c = newFront.get(i);
				int x = c.x();
				int y = c.y();
				int[] adjactentX = {x + 1, x, x, x - 1};
				int[] adjactentY = {y, y - 1, y + 1, y};
				for (int j = 0; j < 4; j++) {
					int thisNumX = adjactentX[j];
					int thisNumY = adjactentY[j];
					if (thisNumX < 0 || thisNumX >= rectangle.width || thisNumY < 0 || thisNumY >= rectangle.height || canceled[thisNumX][thisNumY] != 0) {
						continue;
					}
					if (thisNumX <= 0 || thisNumX >= rectangle.width - 1 || thisNumY <= 0 || thisNumY >= rectangle.height - 1) {
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
					newFront.put(newFront.size(), new BasicCell(thisNumX, thisNumY));
				}
			}
			t++;
		} while (newFront.size() > 0 && t < maxSize);
	}

	public CellCollection newCellCollection(Collection<BasicCell> cls) {
		return new CellCollection(cls, this);
	}

	public int[][] getPathTable(int startX, int startY, int endX, int endY, boolean noDiagonal) {
		int[][] pathTable = new int[rectangle.width][rectangle.height];
		boolean isPathFound = false;
		ArrayList<BasicCell> oldFront = new ArrayList<>();
		ArrayList<BasicCell> newFront = new ArrayList<>();
		newFront.add(new BasicCell(startX, startY));
		for (int i = 0; i < rectangle.width; i++) {
			for (int j = 0; j < rectangle.height; j++) {
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
			newFront = new ArrayList<BasicCell>();
			for (int i = 0; i < oldFront.size(); i++) {
				int x = oldFront.get(i).x();
				int y = oldFront.get(i).y();
				for (int j = 0; j < numOfSides; j++) {
					int thisNumX = x + adjactentX[j];
					int thisNumY = y + adjactentY[j];
					if (thisNumX < 0 || thisNumX >= rectangle.width || thisNumY < 0 || thisNumY >= rectangle.height || pathTable[thisNumX][thisNumY] != 0) {
						continue;
					}
					if (thisNumX == endX && thisNumY == endY) {
						isPathFound = true;
					}
					if (activePlane.getPassability(thisNumX, thisNumY) == Passability.FREE && !(thisNumX == startX && thisNumY == startY)) {
						pathTable[thisNumX][thisNumY] = t + 1;
						newFront.add(new BasicCell(thisNumX, thisNumY));
					}
				}
			}
			t++;
		} while (newFront.size() > 0 && !isPathFound && t < 1000);
		return pathTable;
	}

	public ArrayList<BasicCell> getPath(int startX, int startY, int destinationX, int destinationY, boolean noDiagonal) {
		// �������� ���� �� ������ � ���� ������� ��������� (0 - ������ ��� � �.
		// �.)
		if (destinationX == startX && destinationY == startY) {
			throw new Error("Getting path to itself");
		}
		int[][] pathTable = getPathTable(startX, startY, destinationX, destinationY, noDiagonal);
		ArrayList<BasicCell> path = new ArrayList<BasicCell>();
		if (Cells.isNear(startX, startY, destinationX, destinationY)) {
			path.add(new BasicCell(destinationX, destinationY));
			return path;
		}
		// ���������� ����
		path.add(new BasicCell(startX, startY));
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
			path.add(0, new BasicCell(currentNumX, currentNumY));
			currentNumX = -1;
			for (int i = 0; i < numOfSides; i++) {
				// ��� ������ �� ��������� ������ (�, �, �, �)
				int thisNumX = x + adjactentX[i];
				if (thisNumX < 0 || thisNumX >= rectangle.width) {
					continue;
				}
				int thisNumY = y + adjactentY[i];
				if (thisNumY < 0 || thisNumY >= rectangle.height) {
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
		} else if (startX >= rectangle.width) {
			startX = rectangle.width - 1;
		}
		if (startY < 0) {
			startY = 0;
		} else if (startY >= rectangle.height) {
			startY = rectangle.height - 1;
		}
		if (endX < 0) {
			endX = 0;
		} else if (endX >= rectangle.width) {
			endX = rectangle.width;
		}
		if (endY < 0) {
			endY = 0;
		} else if (endY >= rectangle.height) {
			endY = rectangle.height - 1;
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
		ArrayList<BasicCell> path = getPath(startX, startY, endX, endY, true);
		int size = path.size();
		for (int i = 0; i < size; i++) {
			BasicCell coordinate = path.get(i);
			placeable.place(activePlane, coordinate.x(), coordinate.y());
		}
	}

	protected CellCollection getCoast(int startX, int startY) {
		int[][] pathTable = new int[rectangle.width][rectangle.height];
		ArrayList<BasicCell> cells = new ArrayList<>();
		ArrayList<BasicCell> oldFront = new ArrayList<>();
		ArrayList<BasicCell> newFront = new ArrayList<>();
		newFront.add(new BasicCell(startX, startY));
		for (int i = 0; i < rectangle.width; i++) {
			for (int j = 0; j < rectangle.height; j++) {
				pathTable[i][j] = 0;
			}
		}
		pathTable[startX][startY] = 0;
		int t = 0;
		do {
			oldFront = newFront;
			newFront = new ArrayList<>();
			for (int i = 0; i < oldFront.size(); i++) {
				int x = oldFront.get(i).x();
				int y = oldFront.get(i).y();
				int[] adjactentX = new int[]{x + 1, x, x, x - 1,};
				int[] adjactentY = new int[]{y, y - 1, y + 1, y};
				for (int j = 0; j < 4; j++) {
					int thisNumX = adjactentX[j];
					int thisNumY = adjactentY[j];
					if (thisNumX < 0 || thisNumX >= rectangle.width || thisNumY < 0 || thisNumY >= rectangle.height || pathTable[thisNumX][thisNumY] != 0) {
						continue;
					}
					if (activePlane.getPassability(thisNumX, thisNumY) == Passability.NO && !(thisNumX == startX && thisNumY == startY)) {
						pathTable[thisNumX][thisNumY] = t + 1;
						newFront.add(new BasicCell(thisNumX, thisNumY));
					} else if (activePlane.getPassability(thisNumX, thisNumY) != Passability.NO) {
						cells.add(new BasicCell(x, y));
					}
				}
			}
			t++;
		} while (newFront.size() > 0 && t < 2000);
		return newCellCollection(cells);
	}

	public ArrayList<BasicCell> getCellsAroundCell(int x, int y) {
		ArrayList<BasicCell> answer = new ArrayList<>();
		int x1[] = {x, x + 1, x + 1, x + 1, x, x - 1, x - 1, x - 1};
		int y1[] = {y - 1, y - 1, y, y + 1, y + 1, y + 1, y, y - 1};
		for (int i = 0; i < 8; i++) {
			if (activePlane.getPassability(x1[i], y1[i]) == Passability.FREE) {
				answer.add(new BasicCell(x1[i], y1[i]));
			}
		}
		return answer;
	}

	public void lineToRectangleBorder(
		int startX,
		int startY,
		CardinalDirection side,
		java.awt.Rectangle r,
		TypePlaceableInCell placeable
	) {
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

	public void fillSideOfRectangle(java.awt.Rectangle r, CardinalDirection side, TypePlaceableInCell placeable) {
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
					EntityPlacer.place(activePlane, placeable, rectangle.x + x, rectangle.y + y);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			throw new LocationException("Trying to place entity " + placeable + " outside of location at absolute " +
				"cell " + x + ":" + y + " (relative " + (x - r.x) + ":" + (y - r.y) + ")");
		}
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
		EntityPlacer.place(activePlane, placeable, rectangle.x + x, rectangle.y + y);
	}

	/**
	 * Places a single entity in a cell.
	 *
	 * @param placeable
	 * 	What to place in a cell.
	 * @param point
	 * 	Point in location coordinates.
	 */
	public void place(TypePlaceableInCell placeable, BasicCell point) {
		assert placeable != null : "Trying to place a null object";
		assert point != null;
		EntityPlacer.place(activePlane, placeable, rectangle.x() + point.x(), rectangle.y() + point.y());
	}

	public void lineOfThin(Side line, BorderObjectType type) {
		for (Cell point : line) {
			activePlane.setBorderObject(point.x(), point.y(), line.face(), type);
		}
	}

	public void squareOfThin(Rectangle r, BorderObjectType type) {
		for (int i = 0; i < r.width(); i++) {
			activePlane.setBorderObject(r.getX() + i, r.getY(), Directions.N, type);
			activePlane.setBorderObject(r.getX() + i, r.getY() + r.height() - 1, Directions.S, type);
		}
		for (int i = 0; i < r.height(); i++) {
			activePlane.setBorderObject(r.getX(), r.getY() + i, Directions.W, type);
			activePlane.setBorderObject(r.getX() + r.width() - 1, r.getY() + i, Directions.E, type);
		}
	}

	public void drawCellSet(FiniteCellSet cellSet, TypePlaceableInCell placeable) {
		Objects.requireNonNull(placeable);
		for (BasicCell cell : cellSet) {
			activePlane.place(placeable, rectangle.x + cell.x, rectangle.y + cell.y);
		}
	}

	public void drawCellSet(CellSet cellSet, Rectangle bounds, TypePlaceableInCell placeable) {
		for (BasicCell cell : bounds) {
			if (cellSet.contains(cell)) {
				activePlane.place(placeable, cell.x, cell.y);
			}
		}
	}

	/**
	 * Returns a rectangle with left corner at 0:0, with width and height corresponding to this Location's width and
	 * height.
	 *
	 * @return A new Rectangle.
	 */
	public Rectangle getRelativeBounds() {
		return new Rectangle(0, 0, rectangle.width, rectangle.height);
	}
}

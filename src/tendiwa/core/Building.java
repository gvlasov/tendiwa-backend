package tendiwa.core;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Coordinate;
import tendiwa.core.terrain.settlements.BuildingPlace;
import tendiwa.core.terrain.settlements.Settlement.RoadSystem.Road;
import tendiwa.geometry.CardinalDirection;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.RectangleSystem;

import java.awt.*;
import java.util.*;


public abstract class Building {
	protected Location settlement;
	protected Collection<EnhancedRectangle> rooms;
	protected TerrainModifier terrainModifier;
	protected EnhancedRectangle lobby;
	protected ArrayList<CardinalDirection> doorSides = new ArrayList<CardinalDirection>();
	protected Coordinate frontDoor;

	protected final int x;
	protected final int y;
	protected final int width;
	protected final int height;
	protected final CardinalDirection frontSide;
	protected final CardinalDirection leftSide;
	protected final CardinalDirection rightSide;
	protected final CardinalDirection backSide;
	
	protected Building(BuildingPlace bp, CardinalDirection direction) {
		x = bp.x;
		y = bp.y;
		width = bp.width;
		height = bp.height;
		frontSide = direction;
		leftSide = direction.clockwiseQuarter();
		rightSide = direction.counterClockwiseQuarter();
		backSide = direction.opposite();
	}
	private static HashMap<String, Class<? extends Building>> buildingClasses;
	/**
	 * ArrayList of rectangleIds
	 */
	private ArrayList<Integer> hallways = new ArrayList<>();

	/**
	 * Sets the main properties of Building. This method must have been the
	 * class'es constructor, but this would require each subclass of building to
	 * have an explicit constructor with the same signature.
	 * 
	 * @param location
	 *            The {@link Location} in which the Building is built.
	 * @param place
	 *            Where exactly in location the Building is built.
	 * @param frontSide
	 *            From which cardinal side is the front door.
	 */
	public Building(Location location, BuildingPlace place, CardinalDirection frontSide) {
		this.x = place.x + 1;
		this.y = place.y + 1;
		this.width = place.width - 2;
		this.height = place.height - 2;
		this.settlement = location;
		for (Road road : place.closeRoads) {
			CardinalDirection side = road.getSideOfRectangle(new Rectangle(x, y, width, height));
			if (!doorSides.contains(side)) {
				doorSides.add(side);
			}
		}
		// frontSide = doorSides.get(0);
		this.frontSide = frontSide;
		leftSide = frontSide.clockwiseQuarter();
		rightSide = frontSide.counterClockwiseQuarter();
		backSide = frontSide.opposite();
	}

	public CardinalDirection getDoorSide() {
		/**
		 * Returns first available door side
		 */
		if (doorSides.size() > 0) {
			return doorSides.get(0);
		} else {
			throw new Error("No available door sides");
		}
	}

	public Coordinate placeDoor(EnhancedRectangle r, CardinalDirection side, int object) {
		/**
		 * Places door in the middle of particular side of room.
		 */
		Coordinate c = r.getMiddleOfSide(side).moveToSide(side, 1);
		settlement.setObject(c.x, c.y, object);
		return c;
	}

	public Coordinate placeDoor(EnhancedRectangle r, CardinalDirection side, CardinalDirection endOfSide, int depth, int object) {
		/**
		 * Places door in the particular cell on particular side of room
		 */
		Coordinate c = r.getCellFromSide(side, endOfSide, depth).moveToSide(side, 1);
		settlement.setObject(c.x, c.y, object);
		return c;
	}

	public Coordinate placeFrontDoor(CardinalDirection side) {
		int objDoorBlue = StaticData.getObjectType("door_blue").getId();
		HashMap<Integer, Integer> cells = findDoorAppropriateCells(side);
		if (cells.size() == 0) {
			throw new Error("Nowhere to place the door from side " + side);
		}
		int dx, dy;
		if (side == CardinalDirection.N || side == CardinalDirection.S) {
			ArrayList<Integer> xes = new ArrayList<>(cells.keySet());
			dx = xes.get(Chance.rand(0, xes.size() - 1));
			dy = cells.get(dx);
			settlement.setObject(dx, dy, objDoorBlue);
		} else {
			ArrayList<Integer> yes = new ArrayList<>(cells.keySet());
			dy = yes.get(Chance.rand(0, yes.size() - 1));
			dx = cells.get(dy);
			settlement.setObject(dx, dy, objDoorBlue);
		}
		switch (side) {
			case N:
				lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx, dy + 1);
				break;
			case E:
				lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx - 1, dy);
				break;
			case S:
				lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx, dy - 1);
				break;
			case W:
				lobby = terrainModifier.getRectangleSystem().findRectangleByCell(dx + 1, dy);
				break;
			default:
				throw new IllegalArgumentException();
				
		}
		if (lobby == null) {
			throw new Error("Can't determine the lobby room because desired cell is not in this rectangle system");
		}
		frontDoor = new Coordinate(dx, dy);
		return frontDoor;
	}

	public Coordinate placeFrontDoor(EnhancedRectangle r, CardinalDirection side) {
		/**
		 * Place front door in the middle of rectangle from particular side.
		 */
		int objDoorBlue = StaticData.getObjectType("door_blue").getId();
		Coordinate doorCoord = r.getMiddleOfSide(side).moveToSide(side, 1);
		settlement.setObject(doorCoord.x, doorCoord.y, objDoorBlue);
		lobby = r;
		frontDoor = doorCoord;
		return frontDoor;
	}

	public HashMap<Integer, Integer> findDoorAppropriateCells(CardinalDirection side) {
		HashMap<Integer, Integer> cells = new HashMap<Integer, Integer>();
		Set<Integer> keys;
		if (side == CardinalDirection.N) {
			for (Rectangle r : rooms) {
				int y = r.y - 1;
				for (int i = r.x; i < r.x + r.width; i++) {
					if (!cells.containsKey(i) || cells.get(i) > y) {
						cells.put(i, y);
					}
				}
			}
			keys = new HashSet<Integer>(cells.keySet());
			for (int x : keys) {
				int y = cells.get(x);
				if (settlement.cells[x][y + 1].object() != 0) {
					cells.remove(x);
				}
			}
		} else if (side == CardinalDirection.E) {
			for (Rectangle r : rooms) {
				int x = r.x + r.width;
				for (int i = r.y; i < r.y + r.height; i++) {
					if (!cells.containsKey(i) || cells.get(i) < x) {
						cells.put(i, x);
					}
				}
			}
			keys = new HashSet<Integer>(cells.keySet());
			for (int y : keys) {
				int x = cells.get(y);
				if (settlement.cells[x - 1][y].object() != 0) {
					cells.remove(y);
				}
			}
		} else if (side == CardinalDirection.S) {
			for (Rectangle r : rooms) {
				int y = r.y + r.height;
				for (int i = r.x; i < r.x + r.width; i++) {
					if (!cells.containsKey(i) || cells.get(i) < y) {
						cells.put(i, y);
					}
				}
			}
			keys = new HashSet<Integer>(cells.keySet());
			for (int x : keys) {
				int y = cells.get(x);
				if (settlement.cells[x][y - 1].object() != 0) {
					cells.remove(x);
				}
			}
		} else if (side == CardinalDirection.W) {
			for (Rectangle r : rooms) {
				int x = r.x - 1;
				for (int i = r.y; i < r.y + r.height; i++) {
					if (!cells.containsKey(i) || cells.get(i) > x) {
						cells.put(i, x);
					}
				}
			}
			keys = new HashSet<Integer>(cells.keySet());
			for (int y : keys) {
				int x = cells.get(y);
				if (settlement.cells[x + 1][y].object() != 0) {
					cells.remove(y);
				}
			}
		}
		return cells;
	}

	public HashMap<Integer, Integer> findDoorAppropriateCells(Rectangle r, CardinalDirection side) {
		HashMap<Integer, Integer> cells = new HashMap<>();
		Set<Integer> keys;
		if (side == CardinalDirection.N) {
			int y = r.y - 1;
			for (int i = r.x; i < r.x + r.width; i++) {
				if (!cells.containsKey(i) || cells.get(i) > y) {
					cells.put(i, y);
				}
			}
			keys = new HashSet<>(cells.keySet());
			for (int x : keys) {
				y = cells.get(x);
				if (settlement.cells[x][y + 1].object() != 0) {
					cells.remove(x);
				}
			}
		} else if (side == CardinalDirection.E) {
			int x = r.x + r.width;
			for (int i = r.y; i < r.y + r.height; i++) {
				if (!cells.containsKey(i) || cells.get(i) < x) {
					cells.put(i, x);
				}
			}
			keys = new HashSet<>(cells.keySet());
			for (int y : keys) {
				x = cells.get(y);
				if (settlement.cells[x - 1][y].object() != 0) {
					cells.remove(y);
				}
			}
		} else if (side == CardinalDirection.S) {
			int y = r.y + r.height;
			for (int i = r.x; i < r.x + r.width; i++) {
				if (!cells.containsKey(i) || cells.get(i) < y) {
					cells.put(i, y);
				}
			}
			keys = new HashSet<>(cells.keySet());
			for (int x : keys) {
				y = cells.get(x);
				if (settlement.cells[x][y - 1].object() != 0) {
					cells.remove(x);
				}
			}
		} else if (side == CardinalDirection.W) {
			int x = r.x - 1;
			for (int i = r.y; i < r.y + r.height; i++) {
				if (!cells.containsKey(i) || cells.get(i) > x) {
					cells.put(i, x);
				}
			}
			keys = new HashSet<Integer>(cells.keySet());
			for (int y : keys) {
				x = cells.get(y);
				if (settlement.cells[x + 1][y].object() != 0) {
					cells.remove(y);
				}
			}
		}
		return cells;
	}

	public TerrainModifier buildBasis(int wallType) {
		TerrainModifier modifier = terrainModifier;
		RectangleSystem rs = modifier.getRectangleSystem();
		// if (notSimpleForm) {
		// if (graph.rectangles.size() > 3) {
		// // graph.initialFindOuterSides();
		// boolean formChanged = false;
		// Set<Integer> keys = graph.outerSides.keySet();
		// for (int k : keys) {
		// ArrayList<Integer> sides = graph.outerSides.get(k);
		// if (sides.size() == 0 || Chance.roll(70)) {
		// continue;
		// }
		// if (!graph.isVertexExclusible(k)) {
		// continue;
		// } else {
		// formChanged = true;
		// graph.excludeRectangle(k);
		// }
		// }
		// if (!formChanged) {
		// graph.excludeRectangle(0);
		// }
		// }
		// }
		// if (setup == BasisBuildingSetup.CONVERT_TO_DIRECTED_TREE) {
		// graph.convertGraphToDirectedTree();
		// } else if (setup == BasisBuildingSetup.KEYPOINTS_BASED) {
		//
		// } else if (setup == BasisBuildingSetup.NOT_BUILD_EDGES) {
		//
		// }

		modifier.drawInnerBorders(1, wallType);
		int floorType = StaticData.getFloorType("stone").getId();
		int objDoorBlue = StaticData.getObjectType("door_blue").getId();
		for (Rectangle r : rs.rectangleList()) {
			fillFloor(r, floorType);
		}
		Graph<EnhancedRectangle, DefaultEdge> graph = rs.getGraph();
		
		for (DefaultEdge e : graph.edgeSet()) {
			Coordinate c = connectRoomsWithDoor(graph.getEdgeSource(e), graph.getEdgeTarget(e), objDoorBlue);
			settlement.setFloor(c.x, c.y, floorType);
		}
		rooms = rs.rectangleList();
		return modifier;
	}

	public TerrainModifier getTerrainModifier(int minRoomSize) {
		return settlement.getTerrainModifier(x, y, width, height, minRoomSize, 1);
	}

	public TerrainModifier setTerrainModifier(RectangleSystem crs) {
		return settlement.getTerrainModifier(crs);
	}

	protected Coordinate connectRoomsWithDoor(Rectangle r1, Rectangle r2, int doorObjectId) {
		int x, y;
		if (r1.x + r1.width + 1 == r2.x || r2.x + r2.width + 1 == r1.x) {
			// Vertical
			x = Math.max(r1.x - 1, r2.x - 1);
			y = Chance.rand(Math.max(r1.y, r2.y), Math.min(r1.y + r1.height - 1, r2.y + r2.height - 1));
		} else {
			// Horizontal
			y = Math.max(r1.y - 1, r2.y - 1); // ��, ��� x ������ max, � �����
												// y - min.
			x = Chance.rand(Math.max(r1.x, r2.x), Math.min(r1.x + r1.width - 1, r2.x + r2.width - 1));
		}
		settlement.setObject(x, y, doorObjectId);
		return new Coordinate(x, y);
	}

	protected void fillFloor(Rectangle r, int floorId) {
		settlement.square(r.x, r.y, r.width, r.height, 0, floorId, true);
	}

	protected ArrayList<Coordinate> getCellsNearWalls(Rectangle r) {
		ArrayList<Coordinate> answer = new ArrayList<>();
		for (int i = r.x + 1; i < r.x + r.width - 1; i++) {
			if (!settlement.isDoor(i, r.y - 1)) {
				answer.add(new Coordinate(i, r.y));
			}
			if (!settlement.isDoor(i, r.y + r.height)) {
				answer.add(new Coordinate(i, r.y + r.height - 1));
			}
		}
		for (int i = r.y + 1; i < r.y + r.height - 1; i++) {
			if (!settlement.isDoor(r.x - 1, i)) {
				answer.add(new Coordinate(r.x, i));
			}
			if (!settlement.isDoor(r.x + r.width, i)) {
				answer.add(new Coordinate(r.x + r.width - 1, i));
			}
		}
		// Checking cells in corners
		if (!settlement.isDoor(r.x, r.y - 1) && !settlement.isDoor(r.x - 1, r.y)) {
			answer.add(new Coordinate(r.x, r.y));
		}
		if (!settlement.isDoor(r.x + r.width - 1, r.y - 1) && !settlement.isDoor(r.x + r.width, r.y)) {
			answer.add(new Coordinate(r.x + r.width - 1, r.y));
		}
		if (!settlement.isDoor(r.x + r.width, r.y + r.height - 1) && !settlement.isDoor(r.x + r.width - 1, r.y + r.height)) {
			answer.add(new Coordinate(r.x + r.width - 1, r.y + r.height - 1));
		}
		if (!settlement.isDoor(r.x, r.y + r.height) && !settlement.isDoor(r.x - 1, r.y + r.height - 1)) {
			answer.add(new Coordinate(r.x, r.y + r.height - 1));
		}
		return answer;
	}

	public ArrayList<Coordinate> getCellsNearDoors(Rectangle r) {
		ArrayList<Coordinate> answer = new ArrayList<>();
		for (int i = r.x + 1; i < r.x + r.width - 1; i++) {
			if (settlement.isDoor(i, r.y - 1)) {
				answer.add(new Coordinate(i, r.y));
			}
			if (settlement.isDoor(i, r.y + r.height)) {
				answer.add(new Coordinate(i, r.y + r.height - 1));
			}
		}
		for (int i = r.y + 1; i < r.y + r.height - 1; i++) {
			if (settlement.isDoor(r.x - 1, i)) {
				answer.add(new Coordinate(r.x, i));
			}
			if (settlement.isDoor(r.x + r.width, i)) {
				answer.add(new Coordinate(r.x + r.width - 1, i));
			}
		}
		// Checking cells in corners
		if (settlement.isDoor(r.x, r.y - 1) || settlement.isDoor(r.x - 1, r.y)) {
			answer.add(new Coordinate(r.x, r.y));
		}
		if (settlement.isDoor(r.x + r.width - 1, r.y - 1) || settlement.isDoor(r.x + r.width, r.y)) {
			answer.add(new Coordinate(r.x + r.width - 1, r.y));
		}
		if (settlement.isDoor(r.x + r.width, r.y + r.height - 1) || settlement.isDoor(r.x + r.width - 1, r.y + r.height)) {
			answer.add(new Coordinate(r.x + r.width - 1, r.y + r.height - 1));
		}
		if (settlement.isDoor(r.x, r.y + r.height) || settlement.isDoor(r.x - 1, r.y + r.height - 1)) {
			answer.add(new Coordinate(r.x, r.y + r.height - 1));
		}
		return answer;
	}
	/**
	 * Mark room as hallway so other rooms will prefer to connect to this room
	 * when buildBasis is called
	 */
	public void markAsHallway(int rectangleId) {
		hallways.add(rectangleId);
	}
	/**
	 * Remove all the objects inside rooms.
	 */
	public void clearBasisInside() {
		for (Rectangle r : terrainModifier.getRectangleSystem().rectangleList()) {
			settlement.square(r.x, r.y, r.width, r.height, TerrainBasics.ELEMENT_OBJECT, StaticData.VOID, true);
		}
	}

	public void setLobby(EnhancedRectangle r) {
		lobby = r;
	}

	public void removeWall(Rectangle r, CardinalDirection side) {
		/**
		 * Removes objects on rectangle's border from side side. Removed objects
		 * are not inside the rectangle, they are outside the rectangle. The
		 * ends of border are remain unremoved. Don't mix this up with
		 * TerrainGenerator.fillSideOfRectangle: that function removes objects
		 * inside the rectangle
		 */
		int startX, startY, endX, endY;
		switch (side) {
			case N:
				startX = r.x;
				startY = r.y - 1;
				endX = r.x + r.width - 1;
				endY = r.y - 1;
				break;
			case E:
				startX = r.x + r.width;
				startY = r.y;
				endX = r.x + r.width;
				endY = r.y + r.height - 1;
				break;
			case S:
				startX = r.x;
				startY = r.y + r.height;
				endX = r.x + r.width - 1;
				endY = r.y + r.height;
				break;
			case W:
				startX = r.x - 1;
				startY = r.y;
				endX = r.x - 1;
				endY = r.y + r.height - 1;
				break;
			default:
				throw new Error("Incorrect side " + side);
		}
		settlement.line(startX, startY, endX, endY, TerrainBasics.ELEMENT_OBJECT, StaticData.VOID);
	}

	public enum BasisBuildingSetup {
		/**
		 * Describes which methods should buildBasis use to done edges of graph
		 */
		NOT_BUILD_EDGES, CONVERT_TO_DIRECTED_TREE, KEYPOINTS_BASED
	}

	public static boolean registerClass(Class<? extends Building> cls) {
		buildingClasses.put(cls.getName(), cls);
		return true;
	}

	public abstract boolean fitsToPlace(BuildingPlace place);

	public abstract void draw();
}

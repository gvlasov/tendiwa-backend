package tendiwa.core;

import tendiwa.core.meta.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class NonPlayerCharacter extends tendiwa.core.Character {
public static final long serialVersionUID = 34638L;
private static final int PATH_TABLE_WIDTH = 41;
private static final int MAX_PATH_TABLE_DEPTH = 20;
private final HashMap<Character, Coordinate> lastSeenEnemyCoord = new HashMap<>();
private final PathWalkerOverCharacters pathWalkerOverCharacters;
public HashMap<Character, DialoguePoint> dialogues = new HashMap<>();
protected HashSet<Character> seenCharacters = new HashSet<>();
private int destX;
private int destY;
private Character activeEnemy; // Enemy in plain sight
private Character enemyToChase;
private HashSet<Character> unseenEnemies = new HashSet<>();
private int[][] pathTable;
private Dialogue dialogue;

public NonPlayerCharacter(HorizontalPlane plane, CharacterType characterType, int x, int y, String name) {
	super(plane, characterType, x, y, name);
	Chunk chunk = plane.getChunkWithCell(x, y);
	this.chunk = chunk;
	ep = 100;
	maxEp = 100;
	pathTable = new int[PATH_TABLE_WIDTH][PATH_TABLE_WIDTH];
	destX = x;
	destY = y;
	pathWalkerOverCharacters = new PathWalkerOverCharacters();
}

/* Observations */
public void updateObservation(Character character, int x, int y) {
	Coordinate c = lastSeenEnemyCoord.get(character);
	c.x = x;
	c.y = y;
}

public void discoverDeath(Character character) {
	/**
	 * Removes particular craracter from this character's aims private data,
	 * when that particular character is dead
	 */
	if (activeEnemy == character) {
		activeEnemy = null;
	}
	seenCharacters.remove(character);
}

/* NPC behaviour functions */
private void setDestNearEntity(int eX, int eY) {
	// Set character's destX and destY to the closest cell near the given
	// entity
	int dX = this.x - (PATH_TABLE_WIDTH - 1) / 2;
	int dY = this.y - (PATH_TABLE_WIDTH - 1) / 2;
	Coordinate characterCoord = new Coordinate(x, y);
	if (characterCoord.isNear(eX, eY)) {
		destX = x;
		destY = y;
		return;
	}
	int curX = eX;
	int curY = eY;
	int[] dists = new int[]{curX - 1, curY, curX + 1, curY, curX,
		curY - 1, curX, curY + 1, curX + 1, curY + 1, curX - 1,
		curY + 1, curX + 1, curY - 1, curX - 1, curY - 1};
	int dist = Integer.MAX_VALUE;
	int curDestX = -1, curDestY = -1;
	for (int i = 0; i < 8; i++) {
		if (plane.getPassability(dists[i * 2], dists[i * 2 + 1]) == Chunk.Passability.FREE
			&& pathTable[dists[i * 2] - dX][dists[i * 2 + 1] - dY] <= dist
			&& (curDestX == -1 || characterCoord.distance(dists[i * 2],
			dists[i * 2 + 1]) < characterCoord.distance(curDestX, curDestY))
			&& pathTable[dists[i * 2] - dX][dists[i * 2 + 1] - dY] > 0
			&& !(dists[i * 2] == x && dists[i * 2 + 1] == y)) {
			dist = pathTable[dists[i * 2] - dX][dists[i * 2 + 1] - dY];
			curDestX = dists[i * 2];
			curDestY = dists[i * 2 + 1];
		}
	}
	if (curDestX != -1 || curDestY != -1) {
		destX = curDestX;
		destY = curDestY;
	} else {
		// showPathTable();
		destX = x;
		destY = y;
		throw new Error("Could not set dest for " + name + " at " + x + ":"
			+ y + " (dest " + destX + ":" + destY + ") to entity at "
			+ eX + ":" + eY);
	}
}

private boolean getEnemy() {
	/**
	 * Searches for enemy in this.seenCharacters and puts him to
	 * this.activeEnemy
	 *
	 * First priority are enemies who this character can get to (not blocked
	 * by other characters or objects) . If none of such enemies found, then
	 * this.activeEnemy sets to a character who is visible, but not
	 * accessible.
	 *
	 * If no characters found at all, then this.activeEnemy is set to null.
	 */
	activeEnemy = null;
		/*
		 * If character had activeEnemy, but after getEnemy he hasn't found any,
		 * then activeEnemy will remain null.
		 */
	double distanceToClosestCharacter = 9999999;
	Character unreachableEnemy = null;
	Coordinate characterCoord = new Coordinate(x, y);
	for (Character ch : seenCharacters) {
		if (isEnemy(ch)) {
			double distanceToAnotherCharacter = characterCoord.distance(lastSeenEnemyCoord
				.get(ch));
			if (canComeTo(ch.x, ch.y)
				&& (activeEnemy == null || distanceToAnotherCharacter < distanceToClosestCharacter)) {
				activeEnemy = ch;
				distanceToClosestCharacter = distanceToAnotherCharacter;
			}
			if (activeEnemy == null
				&& (unreachableEnemy == null || characterCoord.distance(ch.x, ch.y) < characterCoord.distance(unreachableEnemy.x, unreachableEnemy.y))) {
				unreachableEnemy = ch;
			}
		}
	}
	if (activeEnemy == null && unreachableEnemy != null) {
		activeEnemy = unreachableEnemy;
	}
	return activeEnemy != null;
}

private boolean getEnemyToChase() {
	/**
	 * Sets one of unseen enemies to enemyToChase
	 */
	enemyToChase = null;
		/*
		 * If character had enemyToChase, but after getEnemyToChase he hasn't
		 * found any, then enemyToChase will remain null.
		 */
	Coordinate characterCoord = new Coordinate(x, y);
	double distanceToClosestCharacter = 9999999;
	for (Character ch : unseenEnemies) {
		// Get closest unseen character position
		double distanceToAnotherCharacter = characterCoord.distance(lastSeenEnemyCoord
			.get(ch));
		if (enemyToChase == null
			|| distanceToAnotherCharacter < distanceToClosestCharacter) {
			enemyToChase = ch;
			distanceToClosestCharacter = distanceToAnotherCharacter;
		}
	}
	return enemyToChase != null;
}

/**
 * Looks at enemies in line of sight and decides on which cell should this character step to retreat
 */
private Coordinate getRetreatCoord() {
		/*
		 * Each side corresponds to the index in sides[], 0-7 clockwise from 12
		 * o'clock. If enemy is from that side, then number at corresponding
		 * index ("threat number") increases. Character will retreat to the side
		 * with the least threat number.
		 */
	int sides[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
	for (Character ch : seenCharacters) {
		int dx = ch.x - x;
		int dy = ch.y - y;
		double dMax;
		if (Math.max(Math.abs(dx), Math.abs(dy)) == Math.abs(dx)) {
			dMax = (double) Math.abs(dx);
		} else {
			dMax = (double) Math.abs(dy);
		}

		// dx2 and dy2 may be -1, 0 or 1
		int dx2 = (int) Math.round((double) dx / dMax);
		int dy2 = (int) Math.round((double) dy / dMax);
		// side is side from which current enemy is
		Direction side = Directions.shiftToDirection(dx2, dy2);
		Direction sideR1 = side.clockwise();
		Direction sideR2 = sideR1.clockwise();
		Direction sideR3 = sideR2.clockwise();
		Direction sideL1 = side.counterClockwise();
		Direction sideL2 = sideL1.counterClockwise();
		Direction sideL3 = sideL2.counterClockwise();
		// Increase threat from all the sides except of opposite side
		sides[side.toInt()] = 4;
		sides[sideL1.toInt()] = 3;
		sides[sideR1.toInt()] = 3;
		sides[sideL2.toInt()] = 2;
		sides[sideR2.toInt()] = 2;
		sides[sideL3.toInt()] = 1;
		sides[sideR3.toInt()] = 1;
	}
	// Find index with minumum value and go to that side
	int min = Integer.MAX_VALUE;
	int indexMin = -1;
	int[] d;
	for (int i = 0; i < 8; i++) {
		d = Directions.intToDirection(i).side2d();
		if (plane.getPassability(x + d[0], y + d[1]) != Chunk.Passability.FREE) {
			continue;
		}
		if (sides[i] < min) {
			min = sides[i];
			indexMin = i;
		}
	}
	if (indexMin == -1) {
		throw new Error("Could not find least dangerous side to retreat");
	}
	d = Directions.intToDirection(indexMin).side2d();
	return new Coordinate(x + d[0], y + d[1]);
}

private boolean canShoot() {
	/**
	 * Checks if this character is able to shoot an arrow or other missile.
	 */
	throw new Error("Unimplemented");
}

private boolean canComeTo(int toX, int toY) {
	int dX = this.x - (PATH_TABLE_WIDTH - 1) / 2;
	int dY = this.y - (PATH_TABLE_WIDTH - 1) / 2;
	Coordinate characterCoord = new Coordinate(x, y);
	return characterCoord.isNear(toX, toY) || pathTable[toX - dX][toY - 1 - dY] != 0
		|| pathTable[toX + 1 - dX][toY - 1 - dY] != 0
		|| pathTable[toX + 1 - dX][toY - dY] != 0
		|| pathTable[toX + 1 - dX][toY + 1 - dY] != 0
		|| pathTable[toX - dX][toY + 1 - dY] != 0
		|| pathTable[toX - 1 - dX][toY + 1 - dY] != 0
		|| pathTable[toX - 1 - dX][toY - dY] != 0
		|| pathTable[toX - 1 - dX][toY - 1 - dY] != 0;
}

public void action() {
	getPathTableToAllSeenCharacters();
	Coordinate characterCoord = new Coordinate(x, y);
	if (false) {
		// If hp is too low, then retreat
		Coordinate retreatCoord = getRetreatCoord();
		step(retreatCoord.x, retreatCoord.y);
	} else if (getEnemy()) {
		if (canShoot()) {
			// shootMissile();
			idle();
		} else if (characterCoord.isNear(activeEnemy.x, activeEnemy.y)) {
			// changePlaces(activeEnemy);
			// push(activeEnemy, SideTest.d2side(activeEnemy.x-x,
			// activeEnemy.y-y));
			attack(activeEnemy);
		} else if (canComeTo(activeEnemy.x, activeEnemy.y)) {
			// Get next cell and move
			destX = activeEnemy.x;
			destY = activeEnemy.y;
			setDestNearEntity(activeEnemy.x, activeEnemy.y);
			System.out.println(name + " goes to " + destX + " " + destY
				+ " near " + activeEnemy.x + " " + activeEnemy.y);
			if (destX == x && destY == y) {
				idle();
			} else {
				LinkedList<EnhancedPoint> dest = Paths.getPath(x, y, destX, destY, this, MAX_PATH_TABLE_DEPTH);
				if (dest.getFirst().x != x || dest.getFirst().y != y) {
					step(dest.getFirst().x, dest.getFirst().y);
				} else {
					idle();
				}
			}
		} else {
			// If sees enemy, but path to him is blocked
				/* */// Maybe this part should be main, and main part should be
			// deleted?!
			// If we always use imaginary table.
			int dX = this.x - (PATH_TABLE_WIDTH - 1) / 2;
			int dY = this.y - (PATH_TABLE_WIDTH - 1) / 2;

			PathTable imaginaryPathTable = Paths.getPathTable(x, y, this, MAX_PATH_TABLE_DEPTH);
			imaginaryPathTable.getPath(activeEnemy.x, activeEnemy.y);
			if (!imaginaryPathTable.cellComputed(activeEnemy.x, activeEnemy.y)) {
				// If path is blocked by characters
				imaginaryPathTable = Paths.getPathTable(x, y, pathWalkerOverCharacters, MAX_PATH_TABLE_DEPTH);
				LinkedList<EnhancedPoint> imaginaryPath = imaginaryPathTable.getPath(activeEnemy.x, activeEnemy.y);
				EnhancedPoint firstStep = imaginaryPath.get(0);
				if (plane.getCharacter(firstStep.x, firstStep.y) == null) {
					// If there is no character on first cell of imaginary
					// path, then step there
					step(firstStep.x, firstStep.y);
				} else {
					idle();
				}
			} else {
				idle();
			}
		}
	} else if (getEnemyToChase()) {
		Coordinate lastSeenCoord = lastSeenEnemyCoord.get(enemyToChase);
		PathTable pathTable = Paths.getPathTable(x, y, this, MAX_PATH_TABLE_DEPTH);
		pathTable.getPath(lastSeenCoord.x, lastSeenCoord.y);

		if (!pathTable.cellComputed(lastSeenCoord.x, lastSeenCoord.y)) {
			// If path is blocked by characters
			LinkedList<EnhancedPoint> path = Paths.getPath(x, y, lastSeenCoord.x, lastSeenCoord.y, pathWalkerOverCharacters, MAX_PATH_TABLE_DEPTH);
			EnhancedPoint firstStep = path.getFirst();
			if (plane.getCharacter(firstStep.x, firstStep.y) == null) {
				// If there is no character on first cell of imaginary path,
				// then step there
				step(firstStep.x, firstStep.y);
			} else {
				idle();
			}
		} else {
			idle();
		}
		// // Get next cell and move
		// destX = lastSeenCoord.x;
		// destY = lastSeenCoord.y;
		// ArrayList<Coordinate> dest = getPath(destX, destY);
		// if (!dest.isEmpty() && (dest.get(0).x != x || dest.get(0).y !=
		// y)) {
		// move(dest.get(0).x, dest.get(0).y);
		// } else {
		// idle();
		// }
		//
		if (lastSeenCoord != null && x == lastSeenCoord.x
			&& y == lastSeenCoord.y) {
			// If character reached the point where he saw his enemy last
			// time
			lastSeenEnemyCoord.remove(enemyToChase);
			unseenEnemies.remove(enemyToChase);
			getEnemyToChase();
		}
	} else {
		idle();
	}
	// Wandering
	// destX = x + (Chance.roll(50) ? 1 : -1);
	// destY = y + (Chance.roll(50) ? 1 : -1);
	// while (true) {
	// if (
	// destX>=location.width || destX<0 || destY>=location.height ||
	// destY<0 ||
	// location.cells[destX][destY].object()!=0 ||
	// location.cells[destX][destY].character()!=null
	// ) {
	// destX = x + (Chance.roll(50) ? 1 : -1);
	// destY = y + (Chance.roll(50) ? 1 : -1);
	// } else {
	// break;
	// }
	// }
	// getPathTable();
	// move(destX, destY);
}

/* Getters */
public String toString() {
	return type.getName() + " " + name;
}

/* Overriden methods */
public void move(int x, int y) {
	super.move(x, y, MovingStyle.STEP);
	getVisibleEntities();
}

public void die() {
	super.die();
	timeStream.removeCharacter(this);
}

/* Pathfinding */
public void showPathTable() {
	for (int i = 0; i < PATH_TABLE_WIDTH; i++) {
		for (int j = 0; j < PATH_TABLE_WIDTH; j++) {
			System.out.println(pathTable[j][i] % 10);
		}
		System.out.println();
	}
	System.out.println("------");
}

public boolean getPathTableToAllSeenCharacters() {
	/**
	 * Builds pathTable until paths to all seenCharacters are found or waves
	 * limit is exceeded.
	 */
	int dX = this.x - (PATH_TABLE_WIDTH - 1) / 2;
	int dY = this.y - (PATH_TABLE_WIDTH - 1) / 2;
	ArrayList<Coordinate> oldFront = new ArrayList<Coordinate>();
	ArrayList<Coordinate> newFront = new ArrayList<Coordinate>();
	newFront.add(new Coordinate(x, y));
	for (int i = 0; i < PATH_TABLE_WIDTH; i++) {
		for (int j = 0; j < PATH_TABLE_WIDTH; j++) {
			pathTable[i][j] = 0;
		}
	}
	pathTable[x - dX][y - dY] = 0;
	int t = 0;
	int charactersLeft = seenCharacters.size();
	HashSet<Character> foundCharacters = new HashSet<Character>();
	do {
		oldFront = newFront;
		newFront = new ArrayList<Coordinate>();
		for (int i = 0; i < oldFront.size(); i++) {
			int x = oldFront.get(i).x;
			int y = oldFront.get(i).y;
			int[] adjactentX = new int[]{x + 1, x, x, x - 1, x + 1,
				x + 1, x - 1, x - 1};
			int[] adjactentY = new int[]{y, y - 1, y + 1, y, y + 1,
				y - 1, y + 1, y - 1};
			for (int j = 0; j < 8; j++) {
				int thisNumX = adjactentX[j] - dX;
				int thisNumY = adjactentY[j] - dY;
				if (thisNumX < 0
					|| thisNumX >= PATH_TABLE_WIDTH
					|| thisNumY < 0
					|| thisNumY >= PATH_TABLE_WIDTH
					|| pathTable[thisNumX][thisNumY] != 0
					|| (thisNumX + dX == this.x && thisNumY + dY == this.y)) {
					continue;
				}
				Chunk.Passability passability = plane.getPassability(thisNumX + dX, thisNumY + dY);
				if ((passability == Chunk.Passability.FREE || !initialCanSee(
					thisNumX + dX, thisNumY + dY)
					&& passability != Chunk.Passability.NO)) {
					// Step to cell if character can see it and it is free
					// or character cannot see it and it is not
					// PASSABILITY_NO
					pathTable[thisNumX][thisNumY] = t + 1;
					newFront.add(new Coordinate(adjactentX[j],
						adjactentY[j]));
				} else {
					Character characterInCell = plane.getCharacter(thisNumX + dX, thisNumY + dY);
					if (seenCharacters.contains(characterInCell)
						&& !foundCharacters.contains(characterInCell)) {
						foundCharacters.add(characterInCell);
						charactersLeft--;
					}
				}
			}
		}
		t++;
	} while (charactersLeft > 0 && newFront.size() > 0 && t < 25);
	return true;
}



	/* Dialogues */
	/* Visibility */

/**
 * Checks if this NonPlayerCharacter currently observes another Character
 */
public boolean canSee(Character aim) {
	return seenCharacters.contains(aim);
}

/**
 * Checks if this character can see another character and, if he can, saves that another character as visible for this
 * character.
 *
 * @param aim
 */
void tryToSee(Character aim) {
	if (aim.isAlive() && initialCanSee(aim.x, aim.y)) {
		seenCharacters.add(aim);
		timeStream.addObserver(aim, this);
		if (unseenEnemies.contains(aim)) {
			unseenEnemies.remove(aim);
		}
		if (!lastSeenEnemyCoord.containsKey(aim)) {
			lastSeenEnemyCoord.put(aim, new Coordinate(aim.x, aim.y));
		}
	}
}

/**
 * Checks if this character can see another character and, if he can, saves that another character as visible for this
 * character.
 *
 * @param aim
 */
void tryToUnsee(Character aim) {
	if (aim.isAlive() && !initialCanSee(aim.x, aim.y)) {
		seenCharacters.remove(aim);
		timeStream.removeObserver(aim, this);
		if (isEnemy(aim)) {
			unseenEnemies.add(aim);
		}
	}
}

/**
 * Tries to see/unsee all characters whithin vision range
 */
public void getVisibleEntities() {
	for (Character character : timeStream.characters) {
		// Quickly select characters that could be seen (including this
		// character, because it's just easier to remove this character
		// later)
		if (Math.abs(character.x - x) <= Character.VISION_RANGE
			&& Math.abs(character.y - y) <= Character.VISION_RANGE) {
			// If another character is probably in this character's vision
			// range, try to see that another character
			tryToSee(character);
		}
	}
	// Clone seen characters set (it will be changed inside the next for loop)
	HashSet<Character> seen = new HashSet<Character>(seenCharacters);
	for (Character character : seen) {
		// Unsee all the characters that left this character's field of view.
		tryToUnsee(character);
	}
	// Exclude this character
	seenCharacters.remove(this);
}

/* Dialogues */
public boolean hasDialogue() {
	return dialogue != null;
}

public void setDialogue(Dialogue dialogue) {
	this.dialogue = dialogue;
}

public void proceedToNextDialoguePoint(Character player,
                                       int answerIndex) {
	DialoguePoint prevDP = dialogues.get(player);
	dialogues.put(player, prevDP.getNextPoint(answerIndex, player));
	DialoguePoint curDP = dialogues.get(player);
	if (curDP.action != null) {
		curDP.action.perform(this, player);
	}
	if (prevDP.isAnswerEnding(answerIndex)) {
		// End dialogue
		throw new UnsupportedOperationException();
	} else {
		// Continue dialogue
		throw new UnsupportedOperationException();
	}
}

public void applyConversationStarting(Character player) {
	DialoguePoint startDP;
	if (dialogues.containsKey(player)) {
		startDP = dialogues.get(player);
	} else {
		startDP = dialogue.root;
	}

	dialogues.put(player, startDP);
	throw new UnsupportedOperationException();
}

class PathWalkerOverCharacters implements PathWalker {

	@Override
	public boolean canStepOn(int x, int y) {
		return x >= 0
			&& y >= 0
			&& x < Tendiwa.getWorld().width
			&& y < Tendiwa.getWorld().height
			&& (plane.getPassability(x, y) == Chunk.Passability.FREE
			|| plane.getCharacter(x, y) != null);

	}
}
}

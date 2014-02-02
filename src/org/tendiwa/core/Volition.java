package org.tendiwa.core;

import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.vision.Seer;

import java.util.Iterator;

/**
 * Human-user expresses his intentions to do various things in game via calling corresponding methods of this class.
 */
public class Volition {
private final Seer playerSeer;
private final Character player;

Volition(Seer playerSeer, Character player) {

	this.playerSeer = playerSeer;
	this.player = player;
}

public void requestSurroundings() {
	playerSeer.computeFullVisionCache();
	Tendiwa.getInstance().emitEvent(new EventInitialTerrain(player.getPlane(), playerSeer));
}

public void actionToCell(ActionToCell action, int x, int y) {
	action.act(player, x, y);
}

public void actionWithoutTarget(ActionWithoutTarget action) {
	action.act(player);
}

public void attack(Character aim) {
	player.attack(aim);
}

public void drop(Item item) {
	if (!player.getInventory().contains(item)) {
		throw new RuntimeException("Attempt to drop an item that PlayerCharacter doens't have");
	}
	player.drop(item);
}

public void idle() {
	player.idle();
}

public void pickUp() {
	HorizontalPlane plane = player.getPlane();
	Iterator<Item> iterator = plane.getItems(player.getX(), player.getY()).iterator();
	if (iterator.hasNext()) {
		player.pickUp(iterator.next());
	}
}

public void putOn(UniqueItem item) {
	player.putOn(item);
}

public void say(String speech) {
	player.say(speech);
}

public void shoot(UniqueItem rangedWeapon, Item projectile, int toX, int toY) {
	if (!Items.isShootable(projectile.getType())) {
		throw new RuntimeException("Projectile must be an item with shootable type");
	}
	player.shoot(rangedWeapon, projectile, toX, toY);
}

public void takeOff(UniqueItem item) {
	player.takeOff(item);
}

public void propel(Item item, int x, int y) {
	player.propel(item, x, y);
}

public void unwield(Item item) {
	player.cease(item);
}

public void move(Direction direction) {
	int[] coords = direction.side2d();
	player.move(player.x + coords[0], player.y + coords[1], MovingStyle.STEP);
}

public void wield(Item item) {
	player.wield(item);
}
}
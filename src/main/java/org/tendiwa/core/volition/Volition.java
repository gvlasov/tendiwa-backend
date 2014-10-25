package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.events.EventInitialTerrain;
import org.tendiwa.core.observation.Observable;

/**
 * Human-user expresses his intentions to do various things in game via calling corresponding methods of this class.
 */
public class Volition {
	private final Observable model;
	private final org.tendiwa.core.Character player;
	private final World world;
	private final RequestShoot.Factory requestShootFactory;
	private final RequestMove.Factory requestMoveFactory;
	private final RequestPickUp.Factory requestPickUpFactory;
	private final RequestPropel.Factory requestPropelFactory;
	private final RequestActionToCell.Factory requestActionToCellFactory;
	private final RequestActionWithoutTarget.Factory requestActionWithoutTargetFactory;
	private final RequestAttack.Factory requestAttackFactory;
	private final RequestIdle.Factory requestIdleFactory;
	private final Server server;

	@Inject
	Volition(
		@Named("tendiwa") Observable model,
		@Named("player") Character player,
		@Named("current_player_world") World world,
		RequestShoot.Factory requestShootFactory,
		RequestMove.Factory requestMoveFactory,
		RequestPickUp.Factory requestPickUpFactory,
		RequestPropel.Factory requestPropelFactory,
		RequestActionToCell.Factory requestActionToCellFactory,
		RequestActionWithoutTarget.Factory requestActionWithoutTargetFactory,
		RequestAttack.Factory requestAttackFactory,
		RequestIdle.Factory requestIdleFactory,
		Server server
	) {
		// TODO: Allow non-player Volition
		this.model = model;
		this.player = player;
		this.world = world;
		this.requestShootFactory = requestShootFactory;
		this.requestMoveFactory = requestMoveFactory;
		this.requestPickUpFactory = requestPickUpFactory;
		this.requestPropelFactory = requestPropelFactory;
		this.requestActionToCellFactory = requestActionToCellFactory;
		this.requestActionWithoutTargetFactory = requestActionWithoutTargetFactory;
		this.requestAttackFactory = requestAttackFactory;
		this.requestIdleFactory = requestIdleFactory;
		this.server = server;
	}

	public void requestSurroundings() {
		player.getSeer().computeFullVisionCache();
		model.emitEvent(new EventInitialTerrain(player, world, player.getPlane(), player.getSeer()));
	}

	public void actionToCell(ActionToCell action, int x, int y) {
		server.passRequest(requestActionToCellFactory.create(action, x, y));
	}

	public void actionWithoutTarget(ActionWithoutTarget action) {
		server.passRequest(requestActionWithoutTargetFactory.create(action));
	}

	public void attack(Character aim) {
		server.passRequest(requestAttackFactory.create(aim));
	}

	public void drop(Item item) {
		if (!player.getInventory().contains(item)) {
			throw new RuntimeException("Attempt to drop an item that PlayerCharacter doesn't have");
		}
		player.drop(item);
		throw new UnsupportedOperationException();
	}

	public void idle() {
		server.passRequest(requestIdleFactory.create());
	}

	public void pickUp() {
		server.passRequest(requestPickUpFactory.create());
	}

	public void putOn(UniqueItem item) {
		player.putOn(item);
		throw new UnsupportedOperationException();
	}

	public void say(String speech) {
		player.say(speech);
		throw new UnsupportedOperationException();
	}

	public void shoot(UniqueItem rangedWeapon, Item projectile, int toX, int toY) {
		server.passRequest(requestShootFactory.create(rangedWeapon, projectile, toX, toY));
	}

	public void takeOff(UniqueItem item) {
		player.takeOff(item);
		throw new UnsupportedOperationException();
	}

	public void propel(Item item, int x, int y) {
		server.passRequest(requestPropelFactory.create(item, x, y));
	}

	public void unwield(Item item) {
		player.cease(item);
		throw new UnsupportedOperationException();
	}

	public void move(Direction direction) {
		server.passRequest(requestMoveFactory.create(direction));
	}

	public void wield(Item item) {
		player.wield(item);
		throw new UnsupportedOperationException();
	}
}

package org.tendiwa.core.events;

import org.tendiwa.core.Character;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.World;
import org.tendiwa.core.vision.Seer;

public class EventMoveToPlane extends EventInitialTerrain {

	/**
	 * Answer to initial request after World has just been loaded. Collects terrain around PlayerCharacter to send it
	 * to
	 * client for displaying.
	 */
	public EventMoveToPlane(Character player, World world, HorizontalPlane plane, Seer seer) {
		super(player, world, plane, seer);
	}
}

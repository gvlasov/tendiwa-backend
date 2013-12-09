package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.*;

import java.util.Iterator;

public class RequestPickUp implements Request {
@Override
public void process() {
	Character player = Tendiwa.getPlayerCharacter();
	HorizontalPlane plane = player.getPlane();
	Iterator<Item> iterator = plane.getItems(player.getX(), player.getY()).iterator();
	if (iterator.hasNext()) {
		player.pickUp(iterator.next());
	}
}
}

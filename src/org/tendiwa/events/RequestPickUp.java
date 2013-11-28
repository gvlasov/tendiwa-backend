package org.tendiwa.events;

import tendiwa.core.*;

import java.util.Iterator;

public class RequestPickUp implements Request {
@Override
public void process() {
	PlayerCharacter player = Tendiwa.getPlayer();
	HorizontalPlane plane = player.getPlane();
	Iterator<Item> iterator = plane.getItems(player.getX(), player.getY()).iterator();
	if (iterator.hasNext()) {
		player.pickUp(iterator.next());
	}
}
}

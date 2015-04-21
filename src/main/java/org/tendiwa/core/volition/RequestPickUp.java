package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.HorizontalPlane;
import org.tendiwa.core.Item;
import org.tendiwa.core.Request;

import java.util.Iterator;

public class RequestPickUp implements Request {
	private final Character player;

	@Inject
	RequestPickUp(
		@Named("player") Character player
	) {
		this.player = player;
	}

	@Override
	public void process() {
		HorizontalPlane plane = player.getPlane();
		Iterator<Item> iterator = plane.getItems(player.x(), player.y()).iterator();
		if (iterator.hasNext()) {
			player.pickUp(iterator.next());
		}

	}

	public static interface Factory {
		public RequestPickUp create();
	}
}

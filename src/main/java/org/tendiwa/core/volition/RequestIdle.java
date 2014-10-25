package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.Request;

public class RequestIdle implements Request {
	private final Character player;

	@Inject
	RequestIdle(
		@Named("player") Character player
	) {

		this.player = player;
	}

	@Override
	public void process() {
		player.idle();
	}

	public static interface Factory {
		public RequestIdle create();
	}
}

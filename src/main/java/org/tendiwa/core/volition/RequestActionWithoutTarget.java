package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.ActionWithoutTarget;
import org.tendiwa.core.Character;
import org.tendiwa.core.Request;

public class RequestActionWithoutTarget implements Request {
	private final Character player;
	private final ActionWithoutTarget action;

	@Inject
	RequestActionWithoutTarget(
		@Named("player") Character player,
		@Assisted ActionWithoutTarget action
	) {
		this.player = player;
		this.action = action;
	}

	@Override
	public void process() {
		action.act(player);
	}

	public static interface Factory {
		public RequestActionWithoutTarget create(ActionWithoutTarget action);
	}
}

package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.ActionToCell;
import org.tendiwa.core.Character;
import org.tendiwa.core.Request;

public class RequestActionToCell implements Request {
	private final Character player;
	private final ActionToCell action;
	private final int x;
	private final int y;

	@Inject
	RequestActionToCell(
		@Named("player") Character player,
		@Assisted ActionToCell action,
		@Assisted("x") int x,
		@Assisted("y") int y
	) {
		this.player = player;
		this.action = action;
		this.x = x;
		this.y = y;
	}

	@Override
	public void process() {
		action.act(player, x, y);
	}

	public static interface Factory {
		public RequestActionToCell create(ActionToCell action, @Assisted("x") int x, @Assisted("y") int y);
	}
}

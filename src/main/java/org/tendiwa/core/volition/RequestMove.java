package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.Direction;
import org.tendiwa.core.MovingStyle;
import org.tendiwa.core.Request;

public class RequestMove implements Request {
	private final Direction direction;
	private final Character player;

	@Inject
	RequestMove(
		@Assisted Direction direction,
		@Named("player") Character player

	) {
		this.direction = direction;
		this.player = player;
	}

	@Override
	public void process() {
		int[] coords = direction.side2d();
		player.move(player.getX() + coords[0], player.getY() + coords[1], MovingStyle.STEP);

	}

	public static interface Factory {
		public RequestMove create(Direction direction);
	}
}

package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.Request;

public class RequestAttack implements Request {
private final Character player;
private final Character aim;

@Inject
RequestAttack(
	@Named("player") Character player,
	@Assisted Character aim
) {

	this.player = player;
	this.aim = aim;
}

@Override
public void process() {
	player.attack(aim);
}

public static interface Factory {
	public RequestAttack create(Character aim);
}
}

package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.Item;
import org.tendiwa.core.Request;
import org.tendiwa.core.Character;

public class RequestPropel implements Request {
private final Character player;
private final Item item;
private final int x;
private final int y;

@Inject
RequestPropel(
	@Named("player") Character player,
    @Assisted Item item,
    @Assisted("x") int x,
    @Assisted("y") int y
) {

	this.player = player;
	this.item = item;
	this.x = x;
	this.y = y;
}
@Override
public void process() {
	player.propel(item, x, y);
}
public static interface Factory {
	public RequestPropel create(Item item, @Assisted("x") int x, @Assisted("y") int y);
}
}

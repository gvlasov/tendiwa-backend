package tendiwa.core;

public class RequestWalk implements Request {
private final CardinalDirection dir;

@Override
public void process() {
	int[] coords = dir.side2d();
	PlayerCharacter player = Tendiwa.getPlayer();
	player.move(player.x+coords[0], player.y+coords[1]);
}

public RequestWalk(CardinalDirection dir) {
	this.dir = dir;
}
}

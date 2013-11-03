package tendiwa.core;

public class RequestWalk implements Request {
private final Direction dir;

@Override
public void process() {
	int[] coords = dir.side2d();
	PlayerCharacter player = Tendiwa.getPlayer();
	player.move(player.x+coords[0], player.y+coords[1]);
}

public RequestWalk(Direction dir) {
	this.dir = dir;
}
}

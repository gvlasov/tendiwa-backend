package tendiwa.core;

public class RequestWalk implements Request {
private final Direction dir;

@Override
public void process() {
	int[] coords = dir.side2d();
	Character player = Tendiwa.getPlayerCharacter();
	player.move(player.x+coords[0], player.y+coords[1], MovingStyle.STEP);
}

public RequestWalk(Direction dir) {
	this.dir = dir;
}
}

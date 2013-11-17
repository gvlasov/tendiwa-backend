package tendiwa.core;

public class RenderPlayer {
public int getY() {
	return y;
}

public void setY(int y) {
	this.y = y;
}

public int getX() {
	return x;
}

public void setX(int x) {
	this.x = x;
}

private int x;
	private int y;
	public RenderPlayer(PlayerCharacter player) {
		x = player.x;
		y = player.y;
	}

}

package org.tendiwa.core.clients;

import org.tendiwa.core.Character;

public class RenderPlayer {
	private int x;
	private int y;

	public RenderPlayer(Character player) {
		x = player.getX();
		y = player.getY();
	}

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

}

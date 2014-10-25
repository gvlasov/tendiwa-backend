package org.tendiwa.core.clients;

import org.tendiwa.core.ItemType;

public class RememberedItem {
	private ItemType type;
	private int x;
	private int y;

	public RememberedItem(int x, int y, ItemType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public int getY() {
		return y;
	}

	public ItemType getType() {
		return type;
	}

	public int getX() {
		return x;
	}

}

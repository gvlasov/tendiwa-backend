package org.tendiwa.math;

public final class MutableBasketWithStones implements BasketWithStones {
	private final int capacity;
	private int stones;

	MutableBasketWithStones(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public int stones() {
		return stones;
	}

	@Override
	public int spaceLeft() {
		return capacity - stones;
	}

	void addStone() {
		stones++;
	}

	int capacity() {
		return capacity;
	}
}

package org.tendiwa.core;

public class BorderObject {
	private final BorderObjectType type;

	public BorderObject(BorderObjectType type) {
		this.type = type;
	}

	public BorderObjectType getType() {
		return type;
	}
}

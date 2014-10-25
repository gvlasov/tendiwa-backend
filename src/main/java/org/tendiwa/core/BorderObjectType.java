package org.tendiwa.core;

public class BorderObjectType implements Resourceable {
	public static final BorderObjectType VOID = new BorderObjectType();
	public String name;

	public void name(String name) {
		this.name = name;
	}

	public void isDoor(boolean isDoor) {

	}

	public void isWindow(boolean isWindow) {

	}

	@Override
	public String getResourceName() {
		return name;
	}
}

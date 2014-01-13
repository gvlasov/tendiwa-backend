package org.tendiwa.core;

public class FloorType implements TypePlaceableInCell, Resourceable {
public boolean liquid;
public String name;

public void liquid(boolean liquid) {
	this.liquid = liquid;
}

public void name(String name) {
	assert name != null;
	this.name = name;
}

@Override
public String getResourceName() {
	return name;
}

public boolean isLiquid() {
	return liquid;
}
}

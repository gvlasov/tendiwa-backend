package org.tendiwa.geometry;

public class StepRectangle {

private final int width;
private final int height;

StepRectangle(int width, int height) {
	this.width = width;
	this.height = height;
}

public StepRectangleMinWidth minWidth(int minWidth) {
	return new StepRectangleMinWidth(width, height, minWidth);
}

}

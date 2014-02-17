package org.tendiwa.geometry;

public class StepRectangleMinWidth {
private final int width;
private final int height;
private final int minWidth;

StepRectangleMinWidth(int width, int height, int minWidth) {
	this.width = width;
	this.height = height;
	this.minWidth = minWidth;
}
public RectangleSystem borderWidth(int borderWidth) {
	return RecursivelySplitRectangleSystemFactory.create(0, 0, width, height, minWidth, borderWidth);
}

}

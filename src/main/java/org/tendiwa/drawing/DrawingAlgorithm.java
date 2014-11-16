package org.tendiwa.drawing;

@FunctionalInterface
public interface DrawingAlgorithm<T> {
	public void draw(T what, DrawableInto canvas);

}

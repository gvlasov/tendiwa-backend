package org.tendiwa.drawing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public final class TestCanvasBuilder {
public static final int DEFAULT_SCALE = 3;
/**
 * Width of canvas window in cells. Size of cell is set with {@link TestCanvasBuilder#setScale(int)}.
 */
public static final int DEFAULT_WIDHT = 800;
public static final int DEFAULT_HEIGHT = 600;
public static final int DEFAULT_FPS = 1000;
final HashMap<Class<?>, DrawingAlgorithm<?>> defaultDrawingAlgorithms = new HashMap<>(DefaultDrawingAlgorithms.algorithms);
/**
 * Size of a cell in pixels.
 */
int scale = 1;
private int width = DEFAULT_WIDHT;
private int height = DEFAULT_HEIGHT;
private boolean visibility = true;
private int fps = DEFAULT_FPS;

/**
 * Tells this builder what algorithm will be used in {@link TestCanvas#draw(Object)} to drawWorld a particular class of
 * objects.
 *
 * @param type
 * 	Class of objects that may be drawn.
 * @param algorithm
 * 	Algorithm that will be used by default to drawWorld those objects.
 */
public <T> TestCanvasBuilder setDefaultDrawingAlgorithmForClass(Class<T> type, DrawingAlgorithm<T> algorithm) {
	defaultDrawingAlgorithms.put(type, algorithm);
	return this;
}

/**
 * Sets the displayed length of one cell in pixels. If this method is not called on a builder, scale is set to 1
 * ({@literal 1 cell == 1 pixel}).
 *
 * @param scale
 */
public <T> TestCanvasBuilder setScale(int scale) {
	this.scale = scale;
	return this;
}

/**
 * Sets the size of {@link TestCanvas}'s window in cells (not in pixels!). If this method is not invoked on builder, the
 * window will be 800×600 pixels.
 *
 * @param width
 * @param height
 * @return
 */
public TestCanvasBuilder setSize(int width, int height) {
	this.width = width;
	this.height = height;
	return this;
}

public TestCanvas build() {
	try {
		return new TestCanvas(scale, width, height, defaultDrawingAlgorithms, visibility, fps);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	throw new RuntimeException("Could not create a gif file");
}

public TestCanvasBuilder setVisiblilty(boolean visibility) {
	this.visibility = visibility;
	return this;
}

public TestCanvasBuilder setFps(int fps) {
	this.fps = fps;
	return this;
}
}

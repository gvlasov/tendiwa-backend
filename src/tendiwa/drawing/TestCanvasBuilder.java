package tendiwa.drawing;

import java.util.HashMap;

public final class TestCanvasBuilder {
	public static final int DEFAULT_SCALE = 1;
	public static final int DEFAULT_WIDHT = 800;
	public static final int DEFAULT_HEIGHT = 600;
	final HashMap<Class<?>, DrawingAlgorithm<?>> defaultDrawingAlgorithms = new HashMap<Class<?>, DrawingAlgorithm<?>>(DefaultDrawingAlgorithms.algorithms);
	int scale = 1;
	private int width = DEFAULT_WIDHT;
	private int height = DEFAULT_HEIGHT;

	/**
	 * Tells this builder what algorithm will be used in
	 * {@link TestCanvas#draw(Object)} to draw a particular class of objects.
	 * 
	 * @param type
	 *            Class of objects that may be drawn.
	 * @param algorithm
	 *            Algorithm that will be used by default to draw those objects.
	 */
	public <T> TestCanvasBuilder setDefaultDrawingAlgorithmForClass(Class<T> type, DrawingAlgorithm<T> algorithm) {
		defaultDrawingAlgorithms.put(type, algorithm);
		return this;
	}
	/**
	 * Sets the displayed length of one cell in pixels. If this method is not
	 * called on a builder, scale is set to 1 ({@literal 1 cell == 1 pixel}).
	 * 
	 * @param scale
	 */
	public <T> TestCanvasBuilder setScale(int scale) {
		this.scale = scale;
		return this;
	}
	/**
	 * Sets the size of {@link TestCanvas}'s window. If this method is not
	 * invoked on builder, the window will be 800×600 pixels.
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
		return new TestCanvas(scale, width, height, defaultDrawingAlgorithms);
	}
}

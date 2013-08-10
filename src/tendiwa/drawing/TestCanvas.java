package tendiwa.drawing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import tendiwa.core.Chunk;
import tendiwa.core.HorizontalPlane;
import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.InterrectangularPath;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.RectanglesJunction;

import com.google.common.collect.Iterables;

public final class TestCanvas {
	public final Layer DEFAULT_LAYER;
	public final Layer MIDDLE_LAYER;
	public final Layer TOP_LAYER;
	private final JFrame frame;
	final int scale;
	private final HashMap<Class<?>, DrawingAlgorithm<?>> defaultDrawingAlgorithms;
	protected static Class<? extends TestCanvas> subclass;
	public final Iterator<Color> colors = Iterables.cycle(
		Color.YELLOW,
		Color.GREEN,
		Color.BLACK,
		Color.BLUE,
		Color.ORANGE,
		Color.PINK).iterator();
	private final JLayeredPane panel;
	Graphics graphics;
	BufferedImage image;
	private final int height;
	private final int width;
	Layer currentLayer;

	public TestCanvas() {
		this(
			TestCanvasBuilder.DEFAULT_SCALE,
			TestCanvasBuilder.DEFAULT_WIDHT,
			TestCanvasBuilder.DEFAULT_HEIGHT,
			DefaultDrawingAlgorithms.algorithms);
	}
	TestCanvas(int scale, int width, int height, HashMap<Class<?>, DrawingAlgorithm<?>> defaultDrawingAlgorithms) {
		this.scale = scale;
		this.defaultDrawingAlgorithms = defaultDrawingAlgorithms;
		this.width = width;
		this.height = height;
		frame = new JFrame("tendiwa canvas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JLayeredPane();
		frame.add(panel);
		DEFAULT_LAYER = new Layer();
		MIDDLE_LAYER = new Layer();
		TOP_LAYER = new Layer();
		panel.setLayer(DEFAULT_LAYER.component, 0);
		panel.setLayer(MIDDLE_LAYER.component, 1);
		panel.setLayer(TOP_LAYER.component, 1);
		panel.add(DEFAULT_LAYER.component);
		panel.add(MIDDLE_LAYER.component);
		panel.add(TOP_LAYER.component);
		setSize(width, height);
		panel.setSize(width, height);
		frame.setVisible(true);
	}
	public void setSize(int width, int height) {
		Collection<Component> components = new ArrayList<Component>();
		components.add(frame);
		components.add(panel);
		Collections.addAll(components, panel.getComponents());
		for (Component component : components) {
			component.setSize(width, height);
		}
		frame.pack();
	}
	/**
	 * Sets the layer of image on which pixels will be drawn. This method is
	 * usually called with one layer in the beginning of some drawing method,
	 * and with the {@link TestCanvas#LAYER_DEFAULT} in the end of that method.
	 * 
	 * @param layer
	 *            The layer to draw on.
	 */

	private void setLayer(Layer layer) {
		graphics = layer.graphics;
		image = layer.image;
		currentLayer = layer;
	}
	public void draw(InterrectangularPath path) {
		for (Rectangle r : path) {
			draw(r);
		}
		for (RectanglesJunction junction : path.getJunctions()) {
			draw(junction);
		}
	}
	public void draw(HorizontalPlane plane) {
		Collection<Chunk> chunks = plane.getChunks();
		for (Chunk chunk : chunks) {
			draw(chunk);
		}
	}
	public Dimension getSize(RectangleSystem rs) {
		Set<Integer> farthestPointsX = new HashSet<Integer>();
		Set<Integer> farthestPointsY = new HashSet<Integer>();
		for (EnhancedRectangle r : rs) {
			Point p = r.getCorner(Directions.SE);
			farthestPointsX.add(p.x);
			farthestPointsY.add(p.y);
		}
		int maxX = Collections.max(farthestPointsX);
		int maxY = Collections.max(farthestPointsY);
		return new Dimension(maxX, maxY);
	}

	class Layer {
		final BufferedImage image;
		final Graphics2D graphics;
		private final JComponent component;

		private Layer() {
			image = new BufferedImage(
				TestCanvas.this.width,
				TestCanvas.this.height,
				BufferedImage.TYPE_INT_ARGB);
			graphics = image.createGraphics();
			component = new JComponent() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, null);
				}
			};
		}
	}

	public static String colorName(Color colorParam) {
		try {
			// first read all fields in array
			Field[] field = Class.forName("java.awt.Color").getDeclaredFields();
			for (Field f : field) {
				String colorName = f.getName();
				Class<?> t = f.getType();
				// System.out.println(f.getType());`
				// check only for constants - "public static final Color"
				if (t == java.awt.Color.class) {
					Color defined = (Color) f.get(null);
					if (defined.equals(colorParam)) {
						return colorName.toUpperCase();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "NO_MATCH";
	}
	/**
	 * Draws an object on this test canvas using a particular algorithm, on a
	 * particular {@link Layer}.
	 * 
	 * @param what
	 *            an object to draw.
	 * @param how
	 *            an algorithm to draw the object.
	 * @param where
	 *            a layer on which the object will be drawn.
	 */
	public <T> void draw(T what, DrawingAlgorithm<T> how, Layer where) {
		how.canvas = this;
		setLayer(where);
		how.draw(what);
		panel.repaint();
	}
	/**
	 * Draws an object on this test canvas using a particular
	 * {@link DrawingAlgorithm}, on the layer {@link TestCanvas#DEFAULT_LAYER}.
	 * 
	 * @param what
	 *            an object to draw.
	 * @param how
	 *            an algorithm to draw the object.
	 */
	public <T> void draw(T what, DrawingAlgorithm<T> how) {
		how.canvas = this;
		setLayer(DEFAULT_LAYER);
		how.draw(what);
		panel.repaint();
	}
	public <T> void draw(T what, Layer where) {
		assert what != null;
		assert where != null;
		DrawingAlgorithm<T> how = getDefaultDrawingAlgorithmOfClass(what);
		how.canvas = this;
		setLayer(where);
		how.draw(what);
		panel.repaint();
	}
	/**
	 * <p>
	 * Draws an object on this TestCanvas on its default layer using a
	 * predefined default algorithm.
	 * </p>
	 * <p>
	 * This overloading of {@code draw} method is intended to be the most
	 * convenient to API users for drawing objects on a canvas. However, if you
	 * need runtime safety, you'd probably better use
	 * {@link TestCanvas#draw(Object, DrawingAlgorithm, Layer)}, because it
	 * specifies the exact drawing algorithm, and thus won't throw any
	 * exceptions.
	 * </p>
	 * 
	 * @param what
	 *            an object to draw.
	 * @throws IllegalArgumentException
	 *             if {@code what} can't be drawn because TestCanvas doesn't
	 *             know of any default algorithm to draw objects of {@code what}
	 *             's class.
	 * @see TestCanvasBuilder#setDefaultDrawingAlgorithmForClass(Class,
	 *      DrawingAlgorithm)
	 */
	public <T> void draw(T what) {
		if (what == null) {
			throw new NullPointerException();
		}
		DrawingAlgorithm<T> how = getDefaultDrawingAlgorithmOfClass(what);
		draw(what, how);
		panel.repaint();
	}
	/**
	 * 
	 * @param what
	 *            An object which will later be drawn.
	 * @return A default drawing algorithm for {@code what}'s class, or null if
	 *         an algorithm for that class was not set.
	 * @see TestCanvasBuilder#setDefaultDrawingAlgorithmForClass(Class,
	 *      DrawingAlgorithm)
	 */
	@SuppressWarnings("unchecked")
	private <T> DrawingAlgorithm<T> getDefaultDrawingAlgorithmOfClass(T what) {
		assert what != null;
		Class<? extends Object> classOfWhat = null;
		for (Class<?> cls : defaultDrawingAlgorithms.keySet()) {
			if (cls.isInstance(what)) {
				classOfWhat = cls;
				break;
			}
		}
		if (classOfWhat == null) {
			throw new IllegalArgumentException(
				"This TestCanvas doesn't know of any default DrawingAlgorithm to draw a " + what
					.getClass()
					.getCanonicalName());
		}
		return (DrawingAlgorithm<T>) defaultDrawingAlgorithms.get(classOfWhat);
	}
}

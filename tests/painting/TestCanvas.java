package painting;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.Chunk;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.TerrainBasics;
import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedPoint;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.FuckingTrailRectangleSystem;
import tendiwa.geometry.InterrectangularPath;
import tendiwa.geometry.RectangleSidePiece;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.RectanglesJunction;
import tendiwa.geometry.Segment;

import com.google.common.collect.Iterables;

public abstract class TestCanvas {
	private final Layer LAYER_DEFAULT = new Layer(0);
	private final Layer LAYER_SIDE_PIECES = new Layer(1);
	private final JFrame frame;
	private int numberOfLines;
	private int scale = 1;
	protected static Class<? extends TestCanvas> subclass;
	public final Iterator<Color> colors = Iterables.cycle(
		Color.YELLOW,
		Color.GREEN,
		Color.BLACK,
		Color.BLUE,
		Color.ORANGE,
		Color.PINK).iterator();
	private final JLayeredPane panel;
	private Graphics graphics;
	private BufferedImage image;
	private static final List<Layer> LAYERS_ORDERED = new ArrayList<Layer>();

	public TestCanvas() {
		frame = new JFrame("tendiwa canvas");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new JLayeredPane();
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.add(panel);
		panel.setLayer(LAYER_DEFAULT, 0);
		panel.setLayer(LAYER_SIDE_PIECES, 1);
		panel.add(LAYER_DEFAULT);
		panel.add(LAYER_SIDE_PIECES);
		setLayer(LAYER_DEFAULT);
		setSize(800, 600);
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
	}
	public static void main(String[] args) {
		throw new RuntimeException(
			"You must override main method with a call to visualize()");
	}
	public abstract void paint();
	public void setScale(int scale) {
		this.scale = scale;
	}
	public void draw(RectangleSystem rs) {
		for (Rectangle r : rs) {
			draw(r, getNextColor());
		}
	}
	public void draw(InterrectangularPath path) {
		for (Rectangle r : path) {
			draw(r);
		}
		for (RectanglesJunction junction : path.getJunctions()) {
			draw(junction);
		}
	}
	private void draw(RectanglesJunction junction) {
		throw new NotImplementedException();
	}
	public void draw(FuckingTrailRectangleSystem trs) {
		draw((RectangleSystem) trs);
		for (Point p : trs.getPoints()) {
			draw(p.x, p.y, Color.RED);
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
	public void draw(TerrainBasics tb) {
		for (int x = 0, maxX = tb.getWidth(); x < maxX; x++) {
			for (int y = 0, maxY = tb.getHeight(); y < maxY; y++) {
				draw(tb, x, y);
			}
		}
	}
	private void draw(TerrainBasics tb, int x, int y) {
		int rasterX = tb.x + x;
		int rasterY = tb.y + y;
		if (tb.hasCharacter(x, y)) {
			draw(rasterX, rasterY, Color.YELLOW);
		} else if (tb.hasObject(x, y)) {
			draw(rasterX, rasterY, Color.GRAY);
		} else {
			draw(rasterX, rasterY, Color.GREEN);
		}
	}
	public void draw(Point point, Color color) {
		draw(point.x, point.y, color);
	}
	private void draw(int x, int y, Color color) {
		if (scale == 1) {
			image.setRGB(x, y, color.getRGB());
		} else {
			graphics.setColor(color);
			graphics.fillRect(x * scale, y * scale, scale, scale);
		}
	}
	private void draw(Rectangle r, Color color) {
		graphics.setColor(color);
		graphics.fillRect(
			r.x * scale,
			r.y * scale,
			r.width * scale,
			r.height * scale);
	}
	public Color draw(Rectangle r) {
		Color color = getNextColor();
		draw(r, color);
		return color;
	}
	public Color getNextColor() {
		// if (circularColor.getRed() > 50) {
		// circularColor = new Color(0, 0, 0);
		// } else {
		// circularColor = new Color(circularColor.getRed() +
		// CIRCULAR_COLOR_STEP, circularColor.getGreen() + CIRCULAR_COLOR_STEP,
		// circularColor.getBlue() + CIRCULAR_COLOR_STEP);
		// }
		// return circularColor;
		return colors.next();
	}

	class Layer extends JComponent {
		private static final long serialVersionUID = 3034052558775981663L;
		private final BufferedImage image;
		private final Graphics2D graphics;

		private Layer(int zIndex) {
			image = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_ARGB);
			graphics = image.createGraphics();
			LAYERS_ORDERED.add(zIndex, this);
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, null);
		}
	}

	public void draw(int[] values) {
		Color color1 = Color.blue;
		Color color2 = Color.yellow;
		Color currentColor = color1;
		int paddingLeft = 10;
		int paddingTop = 10;
		int x = paddingLeft;
		int y = (paddingTop + numberOfLines * 9);
		for (int value : values) {
			for (int i = 0; i < value; i++) {
				draw(new Rectangle(x, y, 1, 1), currentColor);
				x += 1;
			}
			if (currentColor == color1) {
				currentColor = color2;
			} else {
				currentColor = color1;
			}
		}
		numberOfLines++;
	}
	/**
	 * This method provides an ability to launch painting of a subclass with
	 * just calling this method in Subclass.main()
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected static void visualize() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement main = stack[stack.length - 1];
		String mainClass = main.getClassName();
		Class<? extends TestCanvas> tcClass = null;
		try {
			tcClass = (Class<? extends TestCanvas>) TestCanvas.class
				.getClassLoader()
				.loadClass(mainClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TestCanvas self1 = null;
		try {
			self1 = tcClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final TestCanvas self = self1;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					self.paint();
				} catch (Exception e) {
					e.printStackTrace();
					// self.frame.dispatchEvent(new WindowEvent(self.frame,
					// WindowEvent.WINDOW_CLOSING));
				}
			}
		});
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
	 * Draws a piece of rectangle's side. Side pieces are drawn in their own
	 * separate layer which is above the default layer, where all the rectangles
	 * themselves are.
	 * 
	 * @param piece
	 */
	public void draw(RectangleSidePiece piece) {
			setLayer(LAYER_SIDE_PIECES);
		for (EnhancedPoint point : piece.getSegment()) {
			point.moveToSide(piece.getDirection());
			if ((point.x + point.y) % 2 == 0) {
				draw(point, Color.RED);
			} else {
				draw(point, Color.BLUE);
			}
//			draw(point.moveToSide(piece.getDirection()), Color.DARK_GRAY);
//			draw(point.moveToSide(piece.getDirection()), Color.LIGHT_GRAY);
		}
			setLayer(LAYER_DEFAULT);
	}
	public void draw(Segment segment) {
		setLayer(LAYER_SIDE_PIECES);
		for (EnhancedPoint point : segment) {
			draw(point, Color.RED);
		}
		setLayer(LAYER_DEFAULT);
	}
}

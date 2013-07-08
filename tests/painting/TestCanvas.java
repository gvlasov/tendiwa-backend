package painting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tendiwa.core.Chunk;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.TerrainBasics;
import tendiwa.core.meta.Side;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.FuckingTrailRectangleSystem;
import tendiwa.geometry.InterrectangularPath;
import tendiwa.geometry.RectangleSystem;
import tendiwa.geometry.RectanglesJunction;

public abstract class TestCanvas {
private final JFrame frame;
private final DrawingPanel panel;
private final Graphics2D graphics;
private Color circularColor = new Color(0, 0, 0);
private int CIRCULAR_COLOR_STEP = 50;
private int numberOfLines;
private int scale = 1;
protected static Class<? extends TestCanvas> subclass;

public TestCanvas() {
	frame = new JFrame("tendiwa canvas");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	panel = new DrawingPanel();
	frame.add(panel);
	graphics = panel.graphics;
	frame.pack();

	frame.setVisible(true);
}
public static void main(String[] args) {
	throw new RuntimeException("You must override main method with a call to visualize()");
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
	// TODO Auto-generated method stub
	
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
		Point p = r.getCorner(Side.SE);
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
public void draw(int x, int y, Color color) {
	if (scale == 1) {
		panel.image.setRGB(x, y, color.getRGB());
	} else {
		graphics.setColor(color);
		graphics.fillRect(x * scale, y * scale, scale, scale);
	}
}

public void draw(Rectangle r, Color color) {
	graphics.setColor(color);
	graphics.fillRect(r.x * scale, r.y * scale, r.width * scale, r.height * scale);
}
public void draw(Rectangle r) {
	draw(r, getNextColor());
}
public Color getNextColor() {
	if (circularColor.getRed() > 50) {
		circularColor = new Color(0, 0, 0);
	} else {
		circularColor = new Color(circularColor.getRed() + CIRCULAR_COLOR_STEP, circularColor.getGreen() + CIRCULAR_COLOR_STEP, circularColor.getBlue() + CIRCULAR_COLOR_STEP);
	}
	return circularColor;
}

class DrawingPanel extends JPanel {
private static final long serialVersionUID = 1L;
private final BufferedImage image;
public final Graphics2D graphics;

public DrawingPanel() {
	setBorder(BorderFactory.createLineBorder(Color.black));
	image = new BufferedImage(1280, 1024, BufferedImage.TYPE_INT_ARGB);
	graphics = image.createGraphics();
}
public void paintComponent(Graphics g) {
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
 * This method provides an ability to launch painting of a subclass with just
 * calling this method in Subclass.main()
 * 
 */
@SuppressWarnings("unchecked")
protected static void visualize() {
	StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	StackTraceElement main = stack[stack.length - 1];
	String mainClass = main.getClassName();
	Class<? extends TestCanvas> tcClass = null;
	try {
		tcClass = (Class<? extends TestCanvas>) TestCanvas.class.getClassLoader().loadClass(mainClass);
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
			self.paint();
		}
	});
}
}

package org.tendiwa.drawing;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Cell;
import org.tendiwa.geometry.EnhancedRectangle;
import org.tendiwa.geometry.RectangleSystem;
import org.tendiwa.core.meta.GifSequenceWriter;

import javax.imageio.IIOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Window where you can draw world, locations or anything for what there is a {@link DrawingAlgorithm} registered in
 * some {@link org.tendiwa.core.Module}. To draw something on a canvas, use its draw() methods.
 */
public final class TestCanvas {
protected static Class<? extends TestCanvas> subclass;
public final Layer DEFAULT_LAYER;
public final Layer MIDDLE_LAYER;
public final Layer TOP_LAYER;
public final Iterator<Color> colors = Iterables.cycle(
	Color.YELLOW,
	Color.GREEN,
	Color.BLACK,
	Color.BLUE,
	Color.ORANGE,
	Color.PINK).iterator();
public final int height;
public final int width;
final int scale;
private final JFrame frame;
private final DefaultDrawingAlgorithms defaultDrawingAlgorithms;
private final JLayeredPane panel;
private final int fps;
Graphics graphics;
BufferedImage image;
Layer currentLayer;
private GifSequenceWriter gifSequenceWriter;
private File tempFile;
private ImageOutputStream imageOutput;

//TestCanvas() throws FileNotFoundException, IOException {
//	this(
//		TestCanvasBuilder.DEFAULT_SCALE,
//		TestCanvasBuilder.DEFAULT_WIDHT,
//		TestCanvasBuilder.DEFAULT_HEIGHT,
//		DefaultDrawingAlgorithms.algorithms,
//		true,
//		TestCanvasBuilder.DEFAULT_FPS);
//}

@Inject
public TestCanvas(
	int scale,
	int width,
	int height,
	DefaultDrawingAlgorithms defaultDrawingAlgorithms,
	boolean visibility,
	int fps
) throws IOException {
	this.scale = scale;
	this.defaultDrawingAlgorithms = defaultDrawingAlgorithms;
	this.width = width;
	this.height = height;
	this.fps = fps;
	frame = new JFrame("tendiwa canvas");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	panel = new JLayeredPane();
	frame.add(panel);
	DEFAULT_LAYER = new Layer();
	MIDDLE_LAYER = new Layer();
	TOP_LAYER = new Layer();
	panel.setLayer(DEFAULT_LAYER.component, 0);
	panel.setLayer(MIDDLE_LAYER.component, 1);
	panel.setLayer(TOP_LAYER.component, 2);
	panel.add(DEFAULT_LAYER.component);
	panel.add(MIDDLE_LAYER.component);
	panel.add(TOP_LAYER.component);
	setSize(width, height);
	panel.setSize(width, height);
	panel.setPreferredSize(new Dimension(width, height));
	frame.setResizable(false);
	frame.pack();
	frame.setVisible(visibility);
	initGifWriter();
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

private void initGifWriter() {
	imageOutput = null;

	try {
		tempFile = File
			.createTempFile("tendiwa_animation", "" + hashCode());
		imageOutput = new FileImageOutputStream(tempFile);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	int imageType = DEFAULT_LAYER.image.getType();
	try {
		this.gifSequenceWriter = new GifSequenceWriter(imageOutput, imageType, 1000 / fps, true);
	} catch (IIOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void setSize(int width, int height) {
	Collection<Component> components = new ArrayList<>();
	components.add(frame);
	components.add(panel);
	Collections.addAll(components, panel.getComponents());
	for (Component component : components) {
		component.setSize(width, height);
	}
}

/**
 * Sets the layer of image on which pixels will be drawn. This method is usually called with one layer in the beginning
 * of some drawing method, and with the default layer in the end of that method.
 *
 * @param layer
 * 	The layer to drawWorld on.
 */

private void setLayer(Layer layer) {
	graphics = layer.graphics;
	image = layer.image;
	currentLayer = layer;
}

public Dimension getSize(RectangleSystem rs) {
	Set<Integer> farthestPointsX = new HashSet<>();
	Set<Integer> farthestPointsY = new HashSet<>();
	for (EnhancedRectangle r : rs) {
		Cell p = r.getCorner(Directions.SE);
		farthestPointsX.add(p.getX());
		farthestPointsY.add(p.getY());
	}
	int maxX = Collections.max(farthestPointsX);
	int maxY = Collections.max(farthestPointsY);
	return new Dimension(maxX, maxY);
}

/**
 * Draws an object on this test canvas using a particular algorithm, on a particular {@link Layer}.
 *
 * @param what
 * 	an object to drawWorld.
 * @param how
 * 	an algorithm to drawWorld the object.
 * @param where
 * 	a layer on which the object will be drawn.
 */
public <T> void draw(T what, DrawingAlgorithm<? super T> how, Layer where) {
	how.canvas = this;
	setLayer(where);
	how.draw(what);
	panel.repaint();
}

/**
 * Draws an object on this test canvas using a particular {@link DrawingAlgorithm}, on the layer {@link
 * TestCanvas#DEFAULT_LAYER}.
 *
 * @param what
 * 	an object to drawWorld.
 * @param how
 * 	an algorithm to drawWorld the object.
 */
public <T> void draw(T what, DrawingAlgorithm<? super T> how) {
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
 * <p> Draws an object on this TestCanvas on its default layer using a predefined default algorithm. </p> <p> This
 * overloading of {@code drawWorld} method is intended to be the most convenient to API users for drawing objects on a
 * canvas. However, if you need runtime safety, you'd probably better use {@link TestCanvas#draw(Object,
 * DrawingAlgorithm, Layer)}, because it specifies the exact drawing algorithm, and thus won't throw any exceptions.
 * </p>
 *
 * @param what
 * 	an object to drawWorld.
 * @throws IllegalArgumentException
 * 	if {@code what} can't be drawn because TestCanvas doesn't know of any default algorithm to drawWorld objects of
 * 	{@code what} 's class.
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
 * @param what
 * 	An object which will later be drawn.
 * @return A default drawing algorithm for {@code what}'s class, or null if an algorithm for that class was not set.
 */
@SuppressWarnings("unchecked")
private <T> DrawingAlgorithm<T> getDefaultDrawingAlgorithmOfClass(T what) {
	assert what != null;
	Class<?> classOfWhat = null;
	for (Class<?> cls : defaultDrawingAlgorithms) {
		if (cls.isInstance(what)) {
			classOfWhat = cls;
			break;
		}
	}
	if (classOfWhat == null) {
		throw new IllegalArgumentException(
			"This TestCanvas doesn't know of any default DrawingAlgorithm to drawWorld a " + what
				.getClass()
				.getCanonicalName());
	}
	return (DrawingAlgorithm<T>) defaultDrawingAlgorithms.get(classOfWhat);
}

public RenderedImage getImage() {
	return image;
}

public void show() {
	frame.setVisible(true);
}

public void saveFrame() {
	try {
		gifSequenceWriter.writeToSequence(image);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

/**
 * <p> Saves animation of frames made with {@link TestCanvas#saveFrame()} to a file with a specified destination. </p>
 * <p/>
 * This method does not create a file, but rather moves a previously formed temporary file to the destination location.
 *
 * @param filename
 */
public void saveAnimation(String filename) {
	if (tempFile == null) {
		throw new IllegalStateException("Before saving animation you need to create one");
	}
	try {
		gifSequenceWriter.close();
		imageOutput.close();
		Files.move(tempFile, new File(filename));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	initGifWriter();
	System.out.println("Animation saved to " + filename);
}

public void clear() {
	graphics.clearRect(0, 0, width, height);
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
		graphics.setBackground(new Color(255, 255, 255, 1));
		component = new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
				Graphics2D g2d = (Graphics2D) g;
			}
		};
	}
}
}

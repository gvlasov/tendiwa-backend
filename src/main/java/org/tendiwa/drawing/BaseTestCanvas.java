package org.tendiwa.drawing;

import com.google.inject.Inject;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Window where you can draw world, locations or anything for what there is a {@link DrawingAlgorithm}.
 */
class BaseTestCanvas implements DrawableInto {
	private static final AffineTransform defaultTransform = new AffineTransform();
	private final BasicStroke singleWidthStroke;
	public static DrawableInto canvas;

	static {
		defaultTransform.setToScale(1, 1);
	}

	public final Layer DEFAULT_LAYER;
	public final Layer MIDDLE_LAYER;
	public final Layer TOP_LAYER;
	final int scale;
	private final JFrame frame;
	private final JLayeredPane panel;
	private final String defaultTitle;
	private final Rectangle pixelBounds;
	Graphics graphics;
	BufferedImage image;
	Layer currentLayer;


	@Inject
	public BaseTestCanvas(
		final int scale,
		int startX,
		int startY,
		int width,
		int height
	) {
		this.scale = scale;
		this.pixelBounds = new BasicRectangle(
			startX * scale,
			startY * scale,
			width * scale,
			height * scale
		);
		defaultTitle = "tendiwa canvas";
		frame = new JFrame(defaultTitle);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
		setSize(pixelBounds.width(), pixelBounds.height());
		panel.setSize(pixelBounds.width(), pixelBounds.height());
		panel.setPreferredSize(new Dimension(pixelBounds.width(), pixelBounds.height()));
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		panel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				frame.setTitle(
					defaultTitle + " " + getClickCoordinatesAsString(e)
				);
			}
		});
		panel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(clickOutput(e));
			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		setLayer(DEFAULT_LAYER);
		singleWidthStroke = new BasicStroke(1f / scale);
		((Graphics2D) graphics).setTransform(defaultTransform);
		((Graphics2D) graphics).setStroke(singleWidthStroke);
		setFont();
	}

	private void setFont() {
		graphics.setFont(new Font("Verdana", Font.PLAIN, 9));
	}

	/**
	 * Returns absolute world coordinates of a cell where a click was made.
	 *
	 * @return "x:y"
	 */
	private String clickOutput(MouseEvent e) {
		return "point.chebyshovDistanceTo(new Point2D("
			+ (pixelBounds.x() + e.getX()) / scale + "," + (pixelBounds.y() + e.getY()) / scale
			+ "))<1.5";
	}

	private String getClickCoordinatesAsString(MouseEvent e) {
		return (pixelBounds.x() + e.getX()) / scale + "," + (pixelBounds.y() + e.getY()) / scale;
	}

	public void close() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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
	 * Sets the layer of image on which pixels will be drawn. This method is usually called with one layer in the
	 * beginning
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
	@Override
	public <T> void draw(T what, DrawingAlgorithm<? super T> how, Layer where) {
		setLayer(where);
		how.draw(what, this);
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
	@Override
	public <T> void draw(T what, DrawingAlgorithm<? super T> how) {
		Objects.requireNonNull(what);
		setLayer(DEFAULT_LAYER);
		how.draw(what, this);
		panel.repaint();
	}

	@Override
	public void draw(Drawable drawable) {
		drawable.drawIn(this);
	}

	public RenderedImage getImage() {
		return image;
	}

	public void show() {
		frame.setVisible(true);
	}

	public void hide() {
		frame.setVisible(false);
	}

	public void clear() {
		graphics.clearRect(0, 0, pixelBounds.width(), pixelBounds.height());
	}

	@Override
	public void fillBackground(Color backgroundColor) {
		setLayer(DEFAULT_LAYER);
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, pixelBounds.width(), pixelBounds.height());
	}

	@Override
	public int getWidth() {
		return pixelBounds.width();
	}

	@Override
	public int getHeight() {
		return pixelBounds.height();
	}

	@Override
	public int getScale() {
		return scale;
	}

	public class Layer {
		final BufferedImage image;
		final Graphics2D graphics;
		private final JComponent component;

		private Layer() {
			image = new BufferedImage(
				BaseTestCanvas.this.pixelBounds.width(),
				BaseTestCanvas.this.pixelBounds.height(),
				BufferedImage.TYPE_INT_ARGB
			);
			graphics = image.createGraphics();
			graphics.setBackground(new Color(255, 255, 255, 1));
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

	@Override
	public void drawCell(int x, int y, Color color) {
		if (scale == 1) {
			if (!pixelBounds.contains(x, y)) {
//                throw new IllegalArgumentException("Point " + x + ":" + y + " is out of bounds of a " + bounds.width + "x" + bounds
//                        .height + " pixels large canvas");

				return;
			}
			image.setRGB(x - pixelBounds.x(), y - pixelBounds.y(), color.getRGB());
		} else {
			graphics.setColor(color);
			graphics.fillRect(
				x * scale - pixelBounds.x(),
				y * scale - pixelBounds.y(),
				scale,
				scale
			);
		}
	}


	@Override
	public void drawRectangle(Rectangle r, Color color) {
		graphics.setColor(color);
		Graphics2D g2d = (Graphics2D) graphics;
		AffineTransform transform = new AffineTransform();
//		transform.setToScale(scale, scale);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(transform);
		g2d.fill(
			new java.awt.Rectangle(
				r.x() * scale - pixelBounds.x(),
				r.y() * scale - pixelBounds.y(),
				r.width() * scale,
				r.height() * scale
			)
		);
		g2d.setTransform(defaultTransform);
	}

	@Override
	public void drawRectangle2D(BasicRectangle2D r, Color color) {
		graphics.setColor(color);
		Graphics2D g2d = (Graphics2D) graphics;
		AffineTransform transform = new AffineTransform();
//		transform.setToScale(scale, scale)
		g2d.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON
		);
//		g2d.setTransform(transform);
		g2d.setTransform(defaultTransform);
		g2d.fill(
			new java.awt.Rectangle(
				(int) Math.round((r.x + 0.5) * scale - pixelBounds.x()),
				(int) Math.round((r.y + 0.5) * scale - pixelBounds.y()),
				(int) Math.round(r.width * scale),
				(int) Math.round(r.height * scale)
			)
		);
		g2d.setTransform(defaultTransform);
	}

	@Override
	public void drawRasterLine(BasicCell p1, BasicCell p2, Color color) {
		for (Cell coordinate : BasicCellSegment.cells(p1.x, p1.y, p2.x, p2.y)) {
			drawCell(coordinate.x(), coordinate.y(), color);
		}
	}


	@Override
	public void drawLine(double startX, double startY, double endX, double endY, Color color) {
		// TODO: Too cumbersome, many objects created instead of computing coordinates only
		Cell[] cells = BasicCellSegment.vector(
			new BasicCell((int) Math.round(startX), (int) Math.round(startY)),
			new BasicCell((int) Math.round(endX), (int) Math.round(endY))
		);
		for (Cell cell : cells) {
			drawCell(cell.x(), cell.y(), color);
		}
	}

	@Override
	public void fillShape(Shape shape, Color color) {
		Graphics2D g2d = (Graphics2D) graphics;
		graphics.setColor(color);
		AffineTransform transform = new AffineTransform();
		transform.translate(-pixelBounds.x(), -pixelBounds.y());
		transform.scale(scale, scale);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(transform);
		g2d.fill(shape);
		g2d.setTransform(defaultTransform);
		g2d.setStroke(singleWidthStroke);
	}

	@Override
	public void drawShape(Shape shape, Color color) {
		Graphics2D g2d = (Graphics2D) graphics;
		graphics.setColor(color);
		AffineTransform transform = new AffineTransform();
		transform.translate(-pixelBounds.x(), -pixelBounds.y());
		transform.scale(scale, scale);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setTransform(transform);
		g2d.draw(shape);
		g2d.setTransform(defaultTransform);
		g2d.setStroke(singleWidthStroke);
	}

	@Override
	public void drawString(String text, Point2D start, Color color) {
		graphics.setColor(color);
		int translatedX = (int) ((start.x()+0.5 - ((double)pixelBounds.x())/scale) * scale);
		int translatedY = (int) ((start.y()+0.5 - ((double)pixelBounds.y())/scale) * scale);
		graphics.drawString(text, translatedX, translatedY);
	}

	@Override
	public int textWidth(String string) {
		return graphics.getFontMetrics().stringWidth(string);
	}

	@Override
	public int textLineHeight() {
		return graphics.getFontMetrics().getHeight();
	}
}

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

import tendiwa.core.Chunk;
import tendiwa.core.EnhancedRectangle;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.TerrainBasics;
import tendiwa.core.meta.Side;
import tendiwa.recsys.RectangleSystem;

public class TestCanvas extends JFrame {
	private static final long serialVersionUID = 1L;
	private final DrawingPanel panel;
	private final Graphics2D graphics;
	private Color rectangleColor = new Color(0, 0, 0);
	private int RECTANGLE_COLOR_STEP = 1;
	public TestCanvas() {
		super("tendiwa canvas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new DrawingPanel();
		add(panel);
		graphics = panel.graphics;
		pack();

		setVisible(true);
	}
	public void draw(RectangleSystem rs) {
		System.out.println(rs);
		for (Rectangle r : rs) {
			draw(r);
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
			System.out.println(r);
		}
		int maxX = Collections.max(farthestPointsX);
		int maxY = Collections.max(farthestPointsY);
		return new Dimension(maxX, maxY);
	}
	public void draw(TerrainBasics tb) {
		for (int x = 0, maxX=tb.getWidth(); x<maxX; x++) {
			for (int y = 0, maxY=tb.getHeight(); y<maxY; y++) {
				draw(tb, x+tb.x, y+tb.y);
			}
		}
	}
	public void draw(TerrainBasics tb, int x, int y) {
		if (tb.hasCharacter(x, y)) {
			panel.image.setRGB(x, y, Color.YELLOW.getRGB());
		} else if (tb.hasObject(x, y)) {
			panel.image.setRGB(x, y, Color.GRAY.getRGB());
		} else {
			panel.image.setRGB(x, y, Color.green.getRGB());
		}
		
	}
			
	public void draw(Rectangle r) {
		graphics.setColor(rectangleColor);
		graphics.fillRect(r.x, r.y, r.width, r.height);
		if (rectangleColor.getRed() > 50) {
			rectangleColor = new Color(0, 0, 0);
		} else {
			rectangleColor = new Color(
					rectangleColor.getRed()+RECTANGLE_COLOR_STEP,
					rectangleColor.getGreen()+RECTANGLE_COLOR_STEP,
					rectangleColor.getBlue()+RECTANGLE_COLOR_STEP
					);
		}
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
}

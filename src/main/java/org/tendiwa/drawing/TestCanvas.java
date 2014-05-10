package org.tendiwa.drawing;

import com.google.inject.Inject;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellLine;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Window where you can draw world, locations or anything for what there is a {@link DrawingAlgorithm} registered in
 * some {@link org.tendiwa.core.Module}. To draw something on a canvas, use its draw() methods.
 */
public final class TestCanvas implements DrawableInto {

    private static final AffineTransform defaultTransform = new AffineTransform();

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
    private final Rectangle bounds;
    Graphics graphics;
    BufferedImage image;
    Layer currentLayer;


    @Inject
    public TestCanvas(
            final int scale,
            int width,
            int height
    ) {
        this.scale = scale;
        this.bounds = new Rectangle(0, 0, width, height);
        defaultTitle = "tendiwa canvas";
        frame = new JFrame(defaultTitle);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        frame.setVisible(true);
        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                frame.setTitle(defaultTitle + " " + e.getX() / scale + ":" + e.getY() / scale);
            }
        });
        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX() / scale + " " + e.getY() / scale);
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
     *         The layer to drawWorld on.
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
     *         an object to drawWorld.
     * @param how
     *         an algorithm to drawWorld the object.
     * @param where
     *         a layer on which the object will be drawn.
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
     *         an object to drawWorld.
     * @param how
     *         an algorithm to drawWorld the object.
     */
    @Override
    public <T> void draw(T what, DrawingAlgorithm<? super T> how) {
        setLayer(DEFAULT_LAYER);
        how.draw(what, this);
        panel.repaint();
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
        graphics.clearRect(0, 0, bounds.width, bounds.height);
    }

    public void fillBackground(Color backgroundColor) {
        setLayer(DEFAULT_LAYER);
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, bounds.width, bounds.height);
    }

    public int getWidth() {
        return bounds.width;
    }

    public int getHeight() {
        return bounds.height;
    }

    class Layer {
        final BufferedImage image;
        final Graphics2D graphics;
        private final JComponent component;

        private Layer() {
            image = new BufferedImage(
                    TestCanvas.this.bounds.width,
                    TestCanvas.this.bounds.height,
                    BufferedImage.TYPE_INT_ARGB);
            graphics = image.createGraphics();
            graphics.setBackground(new Color(255, 255, 255, 1));
            graphics.setStroke(new BasicStroke(scale));
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

    public void drawCell(int x, int y, Color color) {
        if (scale == 1) {
            if (!bounds.contains(x, y)) {
                throw new IllegalArgumentException("Point " + x + ":" + y + " is out of bounds of a " + bounds.width + "x" + bounds
                        .height + " pixels large canvas");

            }
            image.setRGB(x, y, color.getRGB());
        } else {
            graphics.setColor(color);
            graphics.fillRect(
                    x * scale,
                    y * scale,
                    scale,
                    scale);
        }
    }

    public void drawCell(Cell cell, Color color) {
        drawCell(cell.x, cell.y, color);
    }

    public void drawRectangle(Rectangle r, Color color) {
        graphics.setColor(color);
        Graphics2D g2d = (Graphics2D) graphics;
        AffineTransform transform = new AffineTransform();
        transform.setToScale(scale, scale);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setTransform(transform);
        g2d.fill(r.toAwtRectangle());
        g2d.setTransform(defaultTransform);
//	canvas.graphics.fillRect(
//		r.getX() * canvas.scale,
//		r.getY() * canvas.scale,
//		r.getWidth() * canvas.scale,
//		r.getHeight() * canvas.scale);
    }

    public void drawLine(Cell p1, Cell p2, Color color) {
        for (Cell coordinate : CellLine.vector(p1.x, p1.y, p2.x, p2.y)) {
            drawCell(coordinate.x, coordinate.y, color);
        }
    }

    public void drawLine(Segment2D line, Color color) {
        drawLine(line.start.toCell(), line.end.toCell(), color);
    }

    public void drawLine(double startX, double startY, double endX, double endY, Color color) {
        // TODO: Too cumbersome, many objects created instead of computing coordinates only
        CellLine cells = new CellLine(
                new Cell((int) startX, (int) startY),
                new Cell((int) endX, (int) endY)
        );
        for (Cell cell : cells) {
            drawCell(cell.x, cell.y, color);
        }
    }

    protected void fillShape(Shape shape, Color color) {
        Graphics2D g2d = (Graphics2D) graphics;
        graphics.setColor(color);
        AffineTransform transform = new AffineTransform();
        transform.setToScale(scale, scale);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setTransform(transform);
        g2d.fill(shape);
        g2d.setTransform(defaultTransform);
    }

    protected void drawShape(Shape shape, Color color) {
        Graphics2D g2d = (Graphics2D) graphics;
        graphics.setColor(color);
        AffineTransform transform = new AffineTransform();
        transform.setToScale(scale, scale);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setTransform(transform);
        g2d.draw(shape);
        g2d.setTransform(defaultTransform);
    }

    public void drawString(String text, double x, double y, Color color) {
        graphics.setColor(color);
        graphics.drawString(text, (int) x, (int) y);
    }
}

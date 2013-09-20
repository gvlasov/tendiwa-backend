package tendiwa.geometry;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

public class RectangleSequence implements Iterable<EnhancedRectangle>, Placeable {
    private static final AffineTransform TRANSFORM_CLOCKWISE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(1, 0, 0));
    private static final AffineTransform TRANSFORM_COUNTER_CLOCKWISE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(3, 0, 0));
    private static final AffineTransform TRANSFORM_HALF_CIRCLE = new AffineTransform(AffineTransform.getQuadrantRotateInstance(2, 0, 0));
    /**
     * RectangleAreas that are parts of this RectangleSystem.
     */
    protected ArrayList<EnhancedRectangle> content;

    public RectangleSequence() {
        this.content = new ArrayList<>();
    }

    @Override
    public EnhancedRectangle place(RectangleSystemBuilder builder, int x, int y) {
        for (EnhancedRectangle r : content) {
            EnhancedRectangle actualRec = getActualRectangle(r, x, y);
            builder.placeRectangle(actualRec, DSL.atPoint(actualRec.x, actualRec.y));
        }
        EnhancedRectangle bounds = getBounds();
        return new EnhancedRectangle(x, y, bounds.width, bounds.height);
    }

    @Override
    public StepPlaceNextAt repeat(int count) {
        return new StepPlaceNextAt(count, this);
    }

    @Override
    public void prebuild(RectangleSystemBuilder builder) {
    }

    @Override
    public Placeable rotate(Rotation rotation) {
        AffineTransform transform;
        switch (rotation) {
            case CLOCKWISE:
                transform = TRANSFORM_CLOCKWISE;
                break;
            case COUNTER_CLOCKWISE:
                transform = TRANSFORM_COUNTER_CLOCKWISE;
                break;
            case HALF_CIRCLE:
                transform = TRANSFORM_HALF_CIRCLE;
                break;
            default:
                throw new NotImplementedException();
        }
        RectangleSequence newRs = new RectangleSequence();
        for (EnhancedRectangle r : content) {
            newRs.addRectangle(new EnhancedRectangle(transform.createTransformedShape(r).getBounds()));
        }
        return newRs;
    }

    /**
     * Returns a minimum rectangle that contains all rectangles in this template.
     *
     * @return Minimum rectangle that contains all rectangles in this template.
     */
    @Override
    public final EnhancedRectangle getBounds() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Rectangle r : content) {
            if (r.x < minX) {
                minX = r.x;
            }
            if (r.y < minY) {
                minY = r.y;
            }
            if (r.x + r.width - 1 > maxX) {
                maxX = r.x + r.width - 1;
            }
            if (r.y + r.height - 1 > maxY) {
                maxY = r.y + r.height - 1;
            }
        }
        return new EnhancedRectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    /**
     * Transforms a relative-coordinates rectangle to actual-coordinates rectangle.
     *
     * @param r
     *         Rectangle from this template (a rectangle with relative coordinates).
     * @param x
     *         X coordinate of north-west point of actual bounding rectangle.
     * @param y
     *         Y coordinate of north-west point of actual bounding rectangle.
     * @return Actual coordinates rectangle.
     */
    EnhancedRectangle getActualRectangle(EnhancedRectangle r, int x, int y) {
        assert content.contains(r);
        Rectangle boundingRec = getBounds();
        return new EnhancedRectangle(x + r.x - boundingRec.x, y + r.y - boundingRec.y, r.width, r.height);
    }

    public EnhancedRectangle addRectangle(EnhancedRectangle r) {
        content.add(r);
	    return r;
    }
    @Override
    public Iterator<EnhancedRectangle> iterator() {
        return content.iterator();
    }
    /**
     * Returns a random rectangle from this RectangleSystem.
     *
     * @return A random rectangle existing in this system.
     */
    public Rectangle getRandomRectangle() {
        return content.get(Chance.rand(0, content.size() - 1));
    }
}

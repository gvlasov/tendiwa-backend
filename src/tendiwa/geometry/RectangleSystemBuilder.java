package tendiwa.geometry;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedList;

public class RectangleSystemBuilder {
    protected final LinkedList<EnhancedRectangle> rectangles = new LinkedList<>();
    final RectangleSystem rs;
    protected EnhancedRectangle rememberedRectangle;
    private LinkedList<EnhancedRectangle> boundingRecs = new LinkedList<>();
    private EnhancedRectangle rememberedBoundingRec;

    RectangleSystemBuilder(int borderWidth) {
        this.rs = new RectangleSystem(borderWidth);
    }

    public RectangleSystemBuilder place(Placeable what, Placement where) {
        // Prebuild is needed here because otherwise there wouldn't be any
        // implicit way to inherit border width from an enclosing RectangleSystemBuilder
        what.prebuild(this);
        EnhancedRectangle r = where.placeIn(what, this);
        boundingRecs.add(r);
        return this;
    }

    EnhancedRectangle getRectangleByPointer(RectanglePointer pointer) {
        switch (pointer) {
            case FIRST_RECTANGLE:
                return rectangles.getFirst();
            case LAST_RECTANGLE:
                return rectangles.getLast();
            case REMEMBERED_RECTANGLE:
                return rememberedRectangle;
            case LAST_BOUNDING_REC:
                return boundingRecs.getLast();
            case REMEMBERED_BOUNDING_REC:
                return rememberedBoundingRec;
            default:
                throw new NotImplementedException();
        }
    }

    public RectangleSystemBuilder rememberRectangle() {
        rememberedRectangle = rectangles.getLast();
        return this;
    }

    public EnhancedRectangle placeRectangle(int x, int y, int width, int height) {
        EnhancedRectangle r = rs.addRectangle(new EnhancedRectangle(x, y, width, height));
        rectangles.add(r);
        return r;
    }

    public RectangleSystem done() {
        return rs;
    }

    public void placeRectangle(EnhancedRectangle what, Placement where) {
        what.prebuild(this);
        where.placeIn(what, this);
    }

    public RectangleSystemBuilder rememberBoundingRec() {
        rememberedBoundingRec = boundingRecs.getLast();
        return this;
    }
}


package org.tendiwa.geometry;

public abstract class RectangleBuilderTemplate implements Placeable {
    private RectangleSystem rs;
    public abstract RectangleSystem build();
    private RectangleSystem getRectangleSystem() {
        if (rs == null) {
           rs = build();
        }
        return rs;
    }

    @Override
    public Rectangle getBounds() {
        return getRectangleSystem().getBounds();
    }

    @Override
    public Rectangle place(RectangleSystemBuilder builder, int x, int y) {
        return getRectangleSystem().place(builder, x ,y);
    }

    @Override
    public StepPlaceNextAt repeat(int count) {
        return null;
    }

    @Override
    public void prebuild(RectangleSystemBuilder builder) {
    }

    @Override
    public Placeable rotate(Rotation rotation) {
        return getRectangleSystem().rotate(rotation);
    }

@Override
public Iterable<Rectangle> getRectangles() {
	return getRectangleSystem().getRectangles();
}
}

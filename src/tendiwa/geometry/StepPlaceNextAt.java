package tendiwa.geometry;

import static tendiwa.geometry.DSL.*;

public class StepPlaceNextAt {
    private final int count;
    private final Placeable placeable;

    StepPlaceNextAt(int count, Placeable placeable) {
        this.count = count;
        this.placeable = placeable;
    }

    public Placeable placingNextAt(Placement where) {
        return new Placeable() {
            private RectangleSystem rs;
            public RectangleSystemBuilder prebuiltBuilder;

            @Override
            public EnhancedRectangle getBounds() {
                if (rs == null) {
                    throw new IllegalStateException();
                }
                return rs.getBounds();
            }

            @Override
            public EnhancedRectangle place(RectangleSystemBuilder builder, int x, int y) {
                return rs.place(builder, x, y);
            }


            @Override
            public StepPlaceNextAt repeat(int count) {
                return new StepPlaceNextAt(count, this);
            }

            @Override
            public void prebuild(RectangleSystemBuilder builder) {
                RectangleSystemBuilder newBuilder = builder(builder.rs.borderWidth);
                newBuilder.place(placeable, somewhere());
                for (int i = 1; i < count; i++) {
                    newBuilder.place(placeable, where);
                }
                prebuiltBuilder = newBuilder;
                rs = newBuilder.done();
            }

            @Override
            public Placeable rotate(Rotation rotation) {
                return rs.rotate(rotation);
            }
        };
    }
}

package tendiwa.geometry;

public interface Placeable {
    EnhancedRectangle getBounds();
    EnhancedRectangle place(RectangleSystemBuilder builder, int x, int y);
    StepPlaceNextAt repeat(int count);
    void prebuild(RectangleSystemBuilder builder);
    Placeable rotate(Rotation rotation);
}

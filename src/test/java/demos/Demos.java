package demos;

import com.google.inject.Guice;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;

public final class Demos {
    private Demos() {
        throw new UnsupportedOperationException();
    }
    public static TestCanvas createCanvas() {
        return Guice.createInjector(new DrawingModule()).getInstance(TestCanvas.class);
    }
}

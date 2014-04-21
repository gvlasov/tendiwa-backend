package demos.noise;

import com.google.inject.Guice;
import com.sun.prism.shader.DrawCircle_Color_Loader;
import demos.Demos;
import org.tendiwa.core.*;
import org.tendiwa.core.dependencies.WorldProvider;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.groovy.Registry;
import org.tendiwa.noise.Noise;

import java.awt.*;

public class CoastlineDemo {
    private static final int width = 600;
    private static final int height = 400;
    private static TestCanvas canvas;
    private static final int[][] d1 = new int[][]{
            new int[]{-1, -1},
            new int[]{-1, 0},
            new int[]{-1, 1},
            new int[]{0, 1},
            new int[]{1, 1},
            new int[]{1, 0},
            new int[]{1, -1},
            new int[]{0, -1}
    };
    private static final int[][] d2 = new int[][]{
            new int[]{-2, -2},
            new int[]{-2, -1},
            new int[]{-2, 0},
            new int[]{-2, 1},
            new int[]{-2, 2},
            new int[]{-1, 2},
            new int[]{0, 2},
            new int[]{1, 2},
            new int[]{2, 2},
            new int[]{2, 1},
            new int[]{2, 0},
            new int[]{2, -1},
            new int[]{2, -2},
            new int[]{1, -2},
            new int[]{0, -2},
            new int[]{-1, -2},
    };

    public static void main(String[] args) {
        canvas = Demos.createCanvas();
        terrain();
        coastline();
    }

    private static void coastline() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isCoastline(i, j)) {
                    canvas.draw(new Cell(i, j), DrawingCell.withColor(Color.RED));
                }
            }
        }
    }

    private static boolean isCoastline(int x, int y) {
        if (noise(x, y, 6) <= 128) {
            return false;
        }
        for (int[] d : d1) {
            if (noise(x + d[0], y + d[1], 6) <= 128) {
                return false;
            }
        }
        for (int[] d : d2) {
            if (noise(x + d[0], y + d[1], 6) <= 128) {
                return true;
            }
        }
        return false;
    }

    private static void terrain() {
        DrawingAlgorithm<Cell> grass = DrawingCell.withColor(Color.GREEN);
        DrawingAlgorithm<Cell> water = DrawingCell.withColor(Color.BLUE);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                canvas.draw(new Cell(i, j), noise(i, j, 6) > 128 ? grass : water);
            }
        }
    }

    private static int noise(int x, int y, int octave) {
        return Noise.noise(
                ((double) x) / 32,
                ((double) y) / 32,
                octave
        );
    }

}

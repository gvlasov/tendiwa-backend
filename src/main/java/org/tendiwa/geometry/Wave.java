package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.util.function.Consumer;

import static java.util.Objects.*;

/**
 * In a wave-like fashion, iterates over cells that can be reached from {@link #startCell} walking only over {@link
 * #passableCells}.
 */
public class Wave {
    private final Cell startCell;
    private final CellSet passableCells;
    ScatteredMutableCellSet newFront;
    FiniteCellSet previousFront;
    private int[] dx = new int[]{1, 0, 0, 0 - 1, 1, 1, 0 - 1, 0 - 1};
    private int[] dy = new int[]{0, 0 - 1, 1, 0, 1, 0 - 1, 1, 0 - 1};

    public Wave(Cell startCell, CellSet passableCells) {
        this.startCell = requireNonNull(startCell);
        this.passableCells = requireNonNull(passableCells);
    }

    /**
     * Applies a function to all cells of this Wave. Note that calling this method successively on the same Wave does
     * compute those cells again — they are not stored anywhere.
     *
     * @param handler
     *         A function to call on each {@link Cell} of this Wave.
     */
    public void handle(Consumer<Cell> handler) {
        handler.accept(startCell);
        newFront = new ScatteredMutableCellSet();
        previousFront = FiniteCellSet.of(ImmutableSet.of());
        newFront.add(startCell);
        //noinspection StatementWithEmptyBody
        while (nextWave(handler)) {
        }
    }

    private boolean nextWave(Consumer<Cell> handler) {
        FiniteCellSet currentFront = newFront;
        boolean anyCellsFound = false;
        newFront = new ScatteredMutableCellSet();
        for (Cell old : currentFront) {
            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell(old.x + dx[j], old.y + dy[j]);
                if (!previousFront.contains(cell)
                        && !newFront.contains(cell)
                        && !currentFront.contains(cell)
                        && passableCells.contains(cell)
                        ) {
                    newFront.add(cell);
                    handler.accept(cell);
                    anyCellsFound = true;
                }
            }
        }
        previousFront = currentFront;
        return anyCellsFound;
    }
}

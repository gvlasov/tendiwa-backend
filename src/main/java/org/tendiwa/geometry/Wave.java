package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.util.Objects.*;

/**
 * In a wave-like fashion, iterates over cells that can be reached from start cell walking only over passable cells. The
 * closer a cell is in Chebyshev metric to the start cell, the sooner it will be popped out by iterator.
 */
public class Wave implements Iterable<Cell> {
    private CellSet passableCells;
    Set<Cell> newFront = new HashSet<>();
    Set<Cell> previousFront;
    private int[] dx = new int[]{1, 0, 0, 0 - 1, 1, 1, 0 - 1, 0 - 1};
    private int[] dy = new int[]{0, 0 - 1, 1, 0, 1, 0 - 1, 1, 0 - 1};

    Wave(Cell startCell, CellSet passableCells) {
        requireNonNull(startCell);
        this.passableCells = requireNonNull(passableCells);
        previousFront = ImmutableSet.of();
        newFront.add(startCell);
    }

    public static StepGoingOver from(Cell startCell) {
        return new StepGoingOver(startCell);
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            Iterator<Cell> currentWave = newFront.iterator();
            Cell next = findNext();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Cell next() {
                Cell current = next;
                next = findNext();
                return current;
            }

            private Cell findNext() {
                if (!currentWave.hasNext()) {
                    currentWave = nextWave().iterator();
                }
                if (currentWave.hasNext()) {
                    return currentWave.next();
                } else {
                    return null;
                }
            }
        };
    }

    public static class StepGoingOver {
        private final Cell startCell;

        private StepGoingOver(Cell startCell) {

            this.startCell = startCell;
        }

        public Wave goingOver(CellSet passableCells) {
            return new Wave(startCell, passableCells);
        }
    }

    private Set<Cell> nextWave() {
        Set<Cell> currentFront = newFront;
        newFront = new HashSet<>();
        for (Cell old : currentFront) {
            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell(old.x + dx[j], old.y + dy[j]);
                if (!previousFront.contains(cell)
                        && !newFront.contains(cell)
                        && !currentFront.contains(cell)
                        && passableCells.contains(cell)
                        ) {
                    newFront.add(cell);
                }
            }
        }
        previousFront = currentFront;
        return newFront;
    }
}

package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.FiniteCellSet;

import java.util.Collection;

/**
 * Builds {@link org.tendiwa.geometry.extensions.IntershapeNetwork} objects.
 */
public class IntershapeNetworkBuilder {
    private Collection<FiniteCellSet> shapeExitSets;
    private CellSet walkableCells;

    IntershapeNetworkBuilder() {
    }

    @SuppressWarnings("unused")
    public StepShapeExits withShapeExits(Collection<FiniteCellSet> shapeExitSets) {
        IntershapeNetworkBuilder.this.shapeExitSets = shapeExitSets;
        return new StepShapeExits();
    }

    public class StepShapeExits {

        @SuppressWarnings("unused")
        public StepBuild withWalkableCells(CellSet walkableCells) {
            IntershapeNetworkBuilder.this.walkableCells = walkableCells;
            return new StepBuild();
        }
    }

    public class StepBuild {
        public IntershapeNetwork build() {
            return new IntershapeNetwork(
                    walkableCells,
                    shapeExitSets
            );
        }
    }
}

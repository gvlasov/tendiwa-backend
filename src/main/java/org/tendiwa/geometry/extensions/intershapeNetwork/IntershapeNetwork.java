package org.tendiwa.geometry.extensions.intershapeNetwork;

import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.tendiwa.geometry.CellSegment;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.FiniteCellSet;

import java.util.Collection;

/**
 * Builds a graph where vertices are some {@link org.tendiwa.geometry.FiniteCellSet}s, and edges are paths from a Cell
 * in one cell set to a Cell in another one.
 * <p>
 * Shapes are connected with edges in such a way that:
 * <ol>
 * <li>exit cells on shape border are connected to the closest exit cells on another shape's border</li>
 * <li>two CellSets are connected with at most one road.</li>
 * </ol>
 */
public final class IntershapeNetwork {

	public static StepShapeExits withShapeExits(Collection<FiniteCellSet> shapeExitSets) {
		return new StepShapeExits(shapeExitSets);
	}

	public static final class StepShapeExits {
		private Collection<FiniteCellSet> shapeExitSets;

		StepShapeExits(Collection<FiniteCellSet> shapeExitSets) {
			this.shapeExitSets = shapeExitSets;
		}

		public UnmodifiableUndirectedGraph<FiniteCellSet, CellSegment> withWalkableCells(CellSet walkableCells) {
			return new IntershapeNetworkAlgorithm(walkableCells, shapeExitSets).getGraph();
		}
	}
}

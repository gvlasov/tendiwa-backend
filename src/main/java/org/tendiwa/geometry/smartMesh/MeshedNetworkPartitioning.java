package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import lombok.Lazy;
import org.tendiwa.geometry.BasicPolyline;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.PolylineGraph2D;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;
import org.tendiwa.settlements.SettlementGenerationException;

import static org.tendiwa.collections.Collectors.toImmutableSet;

/**
 * The original graph partitioned in groups of subgraphs:
 * <ul>
 * <li>Cycles — {@link MinimumCycleBasis#minimalCyclesSet()}</li>
 * <li>Filaments — {@link MinimumCycleBasis#filamentsSet()}</li>
 * <li>Nested cycles — {@link CycleWithInnerCycles}</li>
 * </ul>
 * <p>
 * Each subgraph contains a subset of edges and vertices of the original graph.
 */
public final class MeshedNetworkPartitioning extends Graph2D_Wr {
	public MeshedNetworkPartitioning(Graph2D graphToCopy) {
		super(graphToCopy);
	}

	@Lazy
	@Override
	public MinimumCycleBasis<Point2D, Segment2D> minimumCycleBasis() {
		return super.minimumCycleBasis();
	}

	@Lazy
	ImmutableSet<OrientedCycle> cycles() {
		if (minimumCycleBasis().minimalCyclesSet().isEmpty()) {
			throw new SettlementGenerationException("A City with 0 city networks was made");
		}
		return minimumCycleBasis()
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new OrientedCycle(cycle, this))
			.collect(toImmutableSet());
	}

	@Lazy
	ImmutableSet<CycleWithInnerCycles> nestedCycles() {
		return cycles()
			.stream()
			.map(cycle -> new CycleWithInnerCycles(cycle, cycles()))
			.collect(toImmutableSet());
	}
}

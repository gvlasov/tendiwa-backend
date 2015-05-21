package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import lombok.Lazy;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;
import org.tendiwa.graphs.graphs2d.SplittableGraph2D;

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
	public MinimumCycleBasis minimumCycleBasis() {
		return super.minimumCycleBasis();
	}


	@Lazy
	ImmutableSet<CycleWithInnerCycles> nestedCycles() {
		return minimumCycleBasis()
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new CycleWithInnerCycles(cycle, minimumCycleBasis().minimalCyclesSet()))
			.collect(toImmutableSet());
	}

	@Lazy
	public ImmutableSet<SplittableGraph2D> cycles() {
		ImmutableSet.Builder<SplittableGraph2D> builder = ImmutableSet.builder();
		for (CycleWithInnerCycles group : nestedCycles()) {
			builder.addAll(group.holes());
			builder.add(group.hull());
		}
		return builder.build();
	}
}

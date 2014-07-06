package org.tendiwa.settlements;

import com.google.common.collect.ImmutableList;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.TwakStraightSkeleton;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class SecondaryRoadNetworkBlock extends EnclosedBlock {
	private final List<Point2D> outline;

	SecondaryRoadNetworkBlock(List<Point2D> outline) {
		super(outline);
		assert outline.size() > 2;
		this.outline = ImmutableList.copyOf(outline);
	}

	public Set<BlockRegion> shrinkToRegions(double depth, Random random) {
		UndirectedGraph<Point2D,Segment2D> cap = TwakStraightSkeleton.create(outline).cap(depth);
		MinimumCycleBasis<Point2D,Segment2D> basis = new MinimumCycleBasis<>(
			cap,
			Point2DVertexPositionAdapter.get()
		);
		if (cap.hashCode() == -318713953) {
			new GraphExplorer(cap);
		}
		Set<BlockRegion> blocks = basis
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new BlockRegion(cycle.vertexList(), random))
			.collect(Collectors.toSet());
//		assert blocks.size() > 0;
		return blocks;
	}



}

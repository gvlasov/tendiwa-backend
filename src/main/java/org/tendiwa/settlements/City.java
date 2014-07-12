package org.tendiwa.settlements;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCell;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphEdgesSelfIntersection;
import org.tendiwa.geometry.extensions.Point2DRowComparator;
import org.tendiwa.graphs.*;

import java.awt.Color;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class City {
	/**
	 * [Kelly section 4.2]
	 * <p>
	 */
	private final UndirectedGraph<Point2D, Segment2D> highLevelRoadGraph;
	private final SampleSelectionStrategy strategy;
	/**
	 * [Kelly beginning of chapter 4.2]
	 */
	private final UndirectedGraph<Point2D, Segment2D> lowLevelRoadGraph;
	/**
	 * [Kelly section 4.2.2]
	 */
	private final double dSample;
	private final int nSample;
	private final double deviationAngleRad;
	private final double approachingPerSample;
	private final Set<Segment2D> highLevelGraphEdges;
	private final ImmutableSet<NetworkWithinCycle> cells;
	private Random random;
	private final int roadsFromPoint;
	private final double connectivity;
	private final double roadSegmentLength;
	private final double snapSize;
	private final int maxStartPointsPerCell;
	private double secondaryRoadNetworkDeviationAngle;
	private double secondaryRoadNetworkRoadLengthDeviation;
	private final boolean favourAxisAlignedSegments;

	/**
	 * @param highLevelRoadGraph
	 * 	[Kelly chapter 4.2, figure 38]
	 * 	<p>
	 * 	A graph that defines city's road network topology.
	 * @param strategy
	 * 	[Kelly section 4.2.3]
	 * 	<p>
	 * 	Strategy that determines the most appropriate road vertex from those sampled.
	 * @param sampleRadius
	 * 	[Kelly section 4.2.2]
	 * 	<p>
	 * @param samplesPerStep
	 * 	[Kelly section 4.2.2]
	 * 	<p>
	 * 	How many samples per step should a {@code strategy} try.
	 * @param deviationAngle
	 * 	[Kelly section 4.2.2]
	 * 	<p>
	 * 	Angle between two samples, in radians.
	 * @param roadsFromPoint
	 * 	[Kelly figure 42, variable ParamDegree]
	 * 	<p>
	 * 	How many lines would normally go from one point of secondary road network.
	 * @param connectivity
	 * 	[Kelly figure 42, variable ParamConnectivity]
	 * 	<p>
	 * 	How likely it is to snap to node or road when possible. When connectivity == 1.0, algorithm will always
	 * 	snap when possible. When connectivity == 0.0, algorithm will never snap.
	 * @param roadSegmentLength
	 * 	[Kelly figure 42, variable ParamSegmentLength]
	 * 	<p>
	 * 	Mean length of secondary network roads.
	 * @param snapSize
	 * 	[Kelly figure 42, variable ParamSnapSize]
	 * 	<p>
	 * 	A radius around secondary roads' end points inside which new end points would snap to existing ones.
	 * @param maxStartPointsPerCell
	 * 	Number of starting points for road generation in each NetworkWithinCycle.
	 * 	<p>
	 * 	A NetworkWithinCycle is not guaranteed to have exactly {@code maxRoadsFromPoint} starting roads, because
	 * 	such amount might not fit into a cell.
	 * 	<p>
	 * 	In [Kelly figure 43] there are 2 starting points.
	 * @param secondaryRoadNetworkDeviationAngle
	 * 	An angle in radians. How much should the secondary network roads should be deviated from the "ideal" net
	 * 	("ideal" is when this parameter is 0.0).
	 * 	<p>
	 * 	Kelly doesn't have this as a parameter, it is implied in [Kelly figure 42] under "deviate newDirection"
	 * 	and "calculate deviated boundaryRoad perpendicular".
	 * @throws IllegalArgumentException
	 * 	If {@code numberOfSamples <= 0} or if {@code deviationAngle == 0 && numberOfSamples >= 1}, or if
	 * 	#lowLevelRoadGraph produced from #highLevelRoadGraph intersects itself.
	 */
	public City(
		RoadGraph highLevelRoadGraph,
		SampleSelectionStrategy strategy,
		double sampleRadius,
		int samplesPerStep,
		double deviationAngle,
		Random random,
		int roadsFromPoint,
		double connectivity,
		double roadSegmentLength,
		double snapSize,
		int maxStartPointsPerCell,
		double secondaryRoadNetworkDeviationAngle,
		double secondaryRoadNetworkRoadLengthDeviation,
		boolean favourAxisAlignedSegments
	) {
		this.favourAxisAlignedSegments = favourAxisAlignedSegments;
		if (Math.abs(secondaryRoadNetworkDeviationAngle) >= Math.PI * 2) {
			throw new IllegalArgumentException("secondaryRoadNetworkDeviationAngle must be in [0; Math.PI*2)");
		}
		if (Math.abs(secondaryRoadNetworkRoadLengthDeviation) >= roadSegmentLength) {
			throw new IllegalArgumentException("secondaryRoadNetworkRoadLengthDeviation can't be greater than " +
				"roadSegmentLength (the former is " + secondaryRoadNetworkDeviationAngle + ", " +
				"the latter is " + roadSegmentLength + ")");
		}
		if (connectivity < 0 || connectivity > 1) {
			throw new IllegalArgumentException("Connectivity must be in range [0.0; 1.0]");
		}
		if (samplesPerStep <= 0) {
			throw new IllegalArgumentException("Number of samples must be >= 1");
		}
		if (deviationAngle == 0 && samplesPerStep > 1) {
			throw new IllegalArgumentException("When deviationAngle is 0, then number of samples may be only 1");
		}
		if (maxStartPointsPerCell < 1) {
			throw new IllegalArgumentException("NumOfStartPoints must be at least 1");
		}
		this.random = new Random(random.nextInt());
		this.roadsFromPoint = roadsFromPoint;
		this.connectivity = connectivity;
		this.roadSegmentLength = roadSegmentLength;
		this.snapSize = snapSize;
		this.secondaryRoadNetworkDeviationAngle = secondaryRoadNetworkDeviationAngle;
		this.secondaryRoadNetworkRoadLengthDeviation = secondaryRoadNetworkRoadLengthDeviation;
		this.maxStartPointsPerCell = maxStartPointsPerCell;

		this.highLevelRoadGraph = highLevelRoadGraph;
		this.strategy = strategy;
		this.dSample = sampleRadius;
		this.nSample = samplesPerStep;
		this.deviationAngleRad = deviationAngle;
		approachingPerSample = Math.cos(deviationAngle);
		highLevelGraphEdges = highLevelRoadGraph.edgeSet();
		lowLevelRoadGraph = buildLowLevelGraph();
		if (!PlanarGraphEdgesSelfIntersection.test(lowLevelRoadGraph)) {
//            canvas = new TestCanvas(1, 800, 600);
			ImmutableCollection<Point2D> allIntersections = PlanarGraphEdgesSelfIntersection
				.findAllIntersections(lowLevelRoadGraph);
            for (Point2D point : allIntersections) {
                TestCanvas.canvas.draw(point.toCell(), DrawingCell.withColorAndSize(Color.RED, 8));
            }
            TestCanvas.canvas.draw(lowLevelRoadGraph, DrawingGraph.withColor(Color.red));
			throw new IllegalArgumentException("Graph intersects itself");
		}


		ImmutableSet.Builder<NetworkWithinCycle> cellsBuilder = ImmutableSet.builder();
		fillBuilderWithCells(cellsBuilder);
		cells = cellsBuilder.build();
		if (cells.isEmpty()) {
			throw new RuntimeException("A City with 0 city cells was made");
		}
	}

	private void fillBuilderWithCells(ImmutableSet.Builder<NetworkWithinCycle> cellsBuilder) {
		MinimumCycleBasis<Point2D, Segment2D> primitives = new MinimumCycleBasis<>(lowLevelRoadGraph, new VertexPositionAdapter<Point2D>() {
			@Override
			public double getX(Point2D vertex) {
				return vertex.x;
			}

			@Override
			public double getY(Point2D vertex) {
				return vertex.y;
			}
		});
		Map<MinimalCycle<Point2D, Segment2D>, SimpleGraph<Point2D, Segment2D>> cellGraphs
			= constructCityCellGraphs(primitives);
		Collection<Segment2D> filamentEdges = new HashSet<>();
		for (Filament<Point2D, Segment2D> filament : primitives.filamentsSet()) {
			for (Segment2D line : filament) {
				filamentEdges.add(line);
			}
		}
		// Sort cycles to get a fixed order of iteration (so a City will be reproducible with the same seed).
		List<MinimalCycle<Point2D, Segment2D>> sortedCycles = cellGraphs
			.keySet()
			.stream()
			.sorted((o1, o2) -> {
				Point2D p1 = o1.vertexList().get(0);
				Point2D p2 = o2.vertexList().get(0);
				int compare = Double.compare(p1.x, p2.x);
				if (compare == 0) {
					int compare1 = Double.compare(p1.y, p2.y);
					assert compare1 != 0;
					return compare1;
				} else {
					return compare;
				}
			})
			.collect(toList());
		for (MinimalCycle<Point2D, Segment2D> cycle : sortedCycles) {
			cellsBuilder.add(new NetworkWithinCycle(
				cellGraphs.get(cycle),
				cycle,
				filamentEdges,
				roadsFromPoint,
				roadSegmentLength,
				snapSize,
				connectivity,
				secondaryRoadNetworkDeviationAngle,
				secondaryRoadNetworkRoadLengthDeviation,
				maxStartPointsPerCell,
				random,
				favourAxisAlignedSegments
			));
		}
	}


	/**
	 * Constructs all the graphs for this City's CityCells.
	 *
	 * @param primitives
	 * 	A MinimumCycleBasis of this City's {@link #lowLevelRoadGraph}.
	 * @return A map from MinimalCycles to CityCells residing in those cycles.
	 */
	private static Map<MinimalCycle<Point2D, Segment2D>, SimpleGraph<Point2D, Segment2D>> constructCityCellGraphs(
		MinimumCycleBasis<Point2D, Segment2D> primitives
	) {
		Set<Filament<Point2D, Segment2D>> filaments = primitives.filamentsSet();
		Map<MinimalCycle<Point2D, Segment2D>, SimpleGraph<Point2D, Segment2D>> answer = new LinkedHashMap<>();
		EnclosedCycleFilter enclosedCycleFilter = new EnclosedCycleFilter(
			primitives
				.minimalCyclesSet()
				.stream()
					// TODO: Do we really need this sorting here?
				.sorted((a, b) ->
						Point2DRowComparator.getInstance().compare(
							a.iterator().next().start,
							b.iterator().next().start
						)
				)
				.collect(toList())
		);
		Collection<MinimalCycle<Point2D, Segment2D>> enclosingCycles = primitives
			.minimalCyclesSet()
			.stream()
			.filter(enclosedCycleFilter)
			.collect(toList());
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles = primitives
			.minimalCyclesSet()
			.stream()
			.filter(a -> !enclosedCycleFilter.test(a))
			.collect(toList());
		for (MinimalCycle<Point2D, Segment2D> cycle : enclosingCycles) {
			answer.put(cycle, constructCityCellGraph(cycle, filaments, enclosedCycles));
		}
		return answer;
	}

	/**
	 * Constructs a graph of low level roads for a {@link NetworkWithinCycle} that resides inside a {@code cycle}.
	 *
	 * @param cycle
	 * 	A MinimalCycle inside which a NetworkWithinCycle resides.
	 * @param filaments
	 * 	All the filaments of {@link #lowLevelRoadGraph}.
	 * @param enclosedCycles
	 * 	All the cycles of {@link #lowLevelRoadGraph}'s MinimalCycleBasis that reside inside other cycles.
	 * @return A graph containing the {@code cycle} and all the {@code filaments}.
	 */
	private static SimpleGraph<Point2D, Segment2D> constructCityCellGraph(
		MinimalCycle<Point2D, Segment2D> cycle,
		Set<Filament<Point2D, Segment2D>> filaments,
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles) {
		SimpleGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
		for (Filament<Point2D, Segment2D> filament : filaments) {
			for (Point2D vertex : filament.vertexList()) {
				graph.addVertex(vertex);
			}
			for (Segment2D line : filament) {
				graph.addEdge(line.start, line.end, line);
			}
		}
		for (Point2D vertex : cycle.vertexList()) {
			graph.addVertex(vertex);
		}
		for (Segment2D edge : cycle) {
			graph.addEdge(edge.start, edge.end, edge);
		}
		for (MinimalCycle<Point2D, Segment2D> enclosedCycle : enclosedCycles) {
			for (Point2D point : enclosedCycle.vertexList()) {
				graph.addVertex(point);
			}
			for (Segment2D edge : enclosedCycle) {
				graph.addEdge(edge.start, edge.end, edge);
			}
		}

		return graph;
	}

	/**
	 * Returns all CityCells of this City.
	 *
	 * @return All CityCells of this City.
	 */
	public ImmutableSet<NetworkWithinCycle> getCells() {
		return cells;
	}

	/**
	 * Red graph in [Kelly page 47, Figure 38]
	 * <p>
	 * Divides edges of {@link #highLevelRoadGraph} in segments, giving them a deviated curvy shape.
	 *
	 * @return A graph of actual straight road segments.
	 */
	private UndirectedGraph<Point2D, Segment2D> buildLowLevelGraph() {
		Collection<Point2D> vertices = new HashSet<>();
		Collection<Segment2D> edges = new ArrayList<>(getMaxRoadSegmentsNumber(highLevelGraphEdges));
		for (Segment2D edge : highLevelGraphEdges) {
			if (!vertices.contains(edge.start)) {
				vertices.add(edge.start);
			}
			Point2D previousVertex = edge.start;
			List<Point2D> verticesAfterStartVertex = buildRoadVertices(edge, dSample * 1.3);
			assert !verticesAfterStartVertex.contains(edge.start);
			for (Point2D vertex : verticesAfterStartVertex) {
				edges.add(new Segment2D(previousVertex, vertex));
				vertices.add(vertex);
				previousVertex = vertex;
			}
			if (!vertices.contains(edge.end)) {
				vertices.add(edge.end);
			}
		}
		return new RoadGraph(vertices, edges);
	}

	/**
	 * Finds out how many road segments of length {@link #dSample} with deviation {@link #deviationAngleRad} can be
	 * placed between ends of all {@link #highLevelRoadGraph}'s edges.
	 * <p>
	 * Used to find the best initial capacity for an ArrayList of road segments.
	 *
	 * @param highLevelGraphEdges
	 * 	Edges of {@link #highLevelRoadGraph}.
	 * @return Maximum amount of road segments that can be placed.
	 */
	private int getMaxRoadSegmentsNumber(Set<Segment2D> highLevelGraphEdges) {
		double estimatedSummaryRoadSegmentsLength = 0;
		for (Segment2D edge : highLevelGraphEdges) {
			estimatedSummaryRoadSegmentsLength += edge.start.distanceTo(edge.end);
		}
		return (int) Math.ceil(estimatedSummaryRoadSegmentsLength / approachingPerSample / dSample);
	}

	/**
	 * [Kelly sections 4.2.1-4.2.2]
	 * <p>
	 * Grows road segments from both sides of {@code} edge towards each other, deviating them as necessary using {@link
	 * #strategy}.
	 *
	 * @param edge
	 * 	Edge between two {@link #highLevelRoadGraph} vertices.
	 * @param dSnap
	 * 	How close should growing sequences come to insert an edge between their ends and thus terminate segments
	 * 	building.
	 * @return A list of vertices between {@code edge.start} and {@code edge.end} <i>not including</i> {@code
	 * edge.start}, <i>but including</i> {@code edge.end}.
	 */
	private List<Point2D> buildRoadVertices(
		Segment2D edge,
		double dSnap
	) {
		assert dSnap > dSample * approachingPerSample;
		assert highLevelGraphEdges.contains(edge);
		boolean isStepFromStart = true;
		LinkedList<Point2D> forwardList = new LinkedList<>();
		LinkedList<Point2D> reverseList = new LinkedList<>();
		reverseList.add(edge.end);
		Point2D forwardSequenceEnd = edge.start;
		Point2D reverseSequenceEnd = edge.end;
		while (forwardSequenceEnd.distanceTo(reverseSequenceEnd) > dSnap) {
			ImmutableCollection<Point2D> fan = getSampleFan(
				isStepFromStart ? forwardSequenceEnd : reverseSequenceEnd,
				isStepFromStart ? reverseSequenceEnd : forwardSequenceEnd,
				nSample,
				deviationAngleRad
			);
			assert fan.size() == nSample : fan + " " + nSample;
			Point2D nextPoint = strategy.selectNextPoint(fan);
			assert fan.contains(nextPoint);
			if (isStepFromStart) {
				forwardList.addLast(nextPoint);
				forwardSequenceEnd = nextPoint;
			} else {
				reverseList.addFirst(nextPoint);
				reverseSequenceEnd = nextPoint;
			}
			isStepFromStart = !isStepFromStart;
		}
		forwardList.addAll(reverseList);
		return forwardList;

	}

	/**
	 * [Kelly section 4.2.2]
	 * <p>
	 * Returns possible endpoints of the sample coming from {@code sampleStart}.
	 *
	 * @param sampleStart
	 * 	Endpoint of the last sample from one side of a road.
	 * @param sampleEnd
	 * 	End point of the last sample from another side of a road.
	 * @param numberOfSamples
	 * 	Number of points to compute and return.
	 * @param deviationAngleRad
	 * 	Angle in which {@code numberOfSamples} points will be evenly distributed,
	 * @return A fan of {@code numberOfSamples} points oriented from {@code sampleStart} to {@code sampleEnd}, with the
	 * first and the last point being at {@code -deviationAngleRad} and {@code deviationAngleRad} accordingly.
	 */
	private ImmutableSet<Point2D> getSampleFan(
		Point2D sampleStart,
		Point2D sampleEnd,
		int numberOfSamples,
		double deviationAngleRad
	) {
		assert deviationAngleRad >= 0 && deviationAngleRad < Math.PI / 4;
		ImmutableSet.Builder<Point2D> fanBuilder = ImmutableSet.builder();
		if (numberOfSamples == 1) {
			double angle = sampleStart.angleTo(sampleEnd);
			fanBuilder.add(
				new Point2D(
					sampleStart.x + dSample * Math.cos(angle),
					sampleStart.y + dSample * Math.sin(angle)
				)
			);
		} else {
			double angleStart = sampleStart.angleTo(sampleEnd) - deviationAngleRad;
			double dAngle = deviationAngleRad * 2 / (numberOfSamples - 1);
			double angle = angleStart;
			for (int i = 0; i < numberOfSamples; i++) {
				fanBuilder.add(
					new Point2D(
						sampleStart.x + dSample * Math.cos(angle), sampleStart.y + dSample * Math.sin(angle)
					)
				);
				angle += dAngle;
			}
		}
		return fanBuilder.build();
	}

	@SuppressWarnings("unused")
	public UndirectedGraph<Point2D, Segment2D> getHighLevelRoadGraph() {
		return highLevelRoadGraph;
	}

	@SuppressWarnings("unused")
	public UndirectedGraph<Point2D, Segment2D> getLowLevelRoadGraph() {
		return lowLevelRoadGraph;
	}

	public Set<SecondaryRoadNetworkBlock> getBlocks() {
		return cells.stream().flatMap(cell -> cell.getEnclosedBlocks().stream()).collect(toSet());
	}
}

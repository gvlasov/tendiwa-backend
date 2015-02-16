package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.util.*;

public class RedundantMethodsFromRPGM {
	// TODO: This field is probably not needed at all.
	private SampleSelectionStrategy strategy;
	private double dSample;
	private int nSample;
	private double deviationAngleRad;
	private double approachingPerSample;
	private Set<Segment2D> highLevelGraphEdges;
	/**
	 * [Kelly section 4.2]
	 */
	private UndirectedGraph<Point2D, Segment2D> highLevelRoadGraph;

	/**
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
	 * @param deviationAngle
	 * 	[Kelly section 4.2.2]
	 * 	<p>
	 */
	RedundantMethodsFromRPGM(
		SampleSelectionStrategy strategy,
		double sampleRadius,
		int samplesPerStep,
		double deviationAngle

	) {
		if (samplesPerStep <= 0) {
			throw new IllegalArgumentException("Number of samples must be >= 1");
		}
		if (deviationAngle == 0 && samplesPerStep > 1) {
			throw new IllegalArgumentException("When deviationAngle is 0, then number of samples may be only 1");
		}
		this.strategy = strategy;
		this.dSample = sampleRadius;
		this.nSample = samplesPerStep;
		this.deviationAngleRad = deviationAngle;
		approachingPerSample = Math.cos(deviationAngle);
		highLevelGraphEdges = highLevelRoadGraph.edgeSet();
	}

	SampleSelectionStrategy stra =
		sampleFan -> {
			int rand = new Random(4).nextInt(sampleFan.size());
			return sampleFan.toArray(new Point2D[sampleFan.size()])[rand];
		};

	/**
	 * Red graph in [Kelly page 47, Figure 38]
	 * <p>
	 * Divides edges of {@link #highLevelRoadGraph} in segments, giving them a deviated curvy shape.
	 *
	 * @return A graph of actual straight road segments.
	 */
	private UndirectedGraph<Point2D, Segment2D> buildLowLevelGraph() {
		Collection<Point2D> vertices = new LinkedHashSet<>();
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
		return createRoadGraph(vertices, edges);
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

	private static UndirectedGraph<Point2D, Segment2D> createRoadGraph(
		Collection<Point2D> vertices,
		Collection<Segment2D> edges
	) {
		UndirectedGraph<Point2D, Segment2D> answer = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		vertices.forEach(answer::addVertex);
		for (Segment2D edge : edges) {
			answer.addEdge(edge.start, edge.end, edge);
		}
		return answer;
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
}

package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Segment2D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MovementRegistry {
	private Map<Segment2D, OppositeEdgeStartMovement> movements = new HashMap<>();
	MovementRegistry(List<Node> nodes) {
		for (Node node : nodes) {
			movements.put(node.currentEdge, new OppositeEdgeStartMovement(node));
		}
	}

	public OppositeEdgeStartMovement getByOriginalEdge(Segment2D edge) {
		return movements.get(edge);
	}
}

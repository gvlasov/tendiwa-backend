package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

final class Face implements Iterable<Node> {
	final Chain startHalfface;
	@Nonnull
	final Chain endHalfface;
	@Nullable
	private Chain chainAtEnd1;
	@Nullable
	private Chain chainAtEnd2;
	private boolean linkAtEnd1First;
	private boolean linkAtEnd2First;
	private Chain lastAddedChain;
	private int numberOfSkeletonNodes;
	private TreeSet<Node> sortedLinkEnds = new TreeSet<>(
		(Node o1, Node o2) -> {
			if (o1 == o2) {
				return 0;
			}
			if (o1.isPair(o2)) {
				SplitNode o1s = (SplitNode) o1;
				SplitNode o2s = (SplitNode) o2;
				assert o1s.isLeft() != o2s.isLeft();
				return o1s.isLeft() ? 1 : -1;
			} else {
				double projection1 = projectionOnEdge(o1);
				double projection2 = projectionOnEdge(o2);
				assert projection1 != projection2;
				return (int) Math.signum(projection1 - projection2);
			}
		}
	);

	Face(OriginalEdgeStart edgeStart, OriginalEdgeStart edgeEnd) {
		assert edgeStart.next() == edgeEnd;
		startHalfface = new Chain(edgeStart, edgeStart, null);
		endHalfface = new Chain(edgeEnd, edgeEnd, startHalfface);
		startHalfface.setNextChain(endHalfface);
		assert startHalfface.nextChain == endHalfface && endHalfface.previousChain == startHalfface
			&& startHalfface.previousChain == null && endHalfface.nextChain == null;
		lastAddedChain = endHalfface;
		numberOfSkeletonNodes = 2; // Initially there are two skeleton nodes: startHalfface start and endHalfface end.
	}

	private boolean isEndOfHalfface(Node end) {
		return end == startHalfface.lastSkeletonNode() || end == endHalfface.lastSkeletonNode();
	}

	private Node getNodeFromLeft(LeftSplitNode leftNode) {
		Node higher = sortedLinkEnds.higher(leftNode);
		if (higher == null) {
//			if (endHalfface.lastSkeletonNode().isProcessed()) {
//				assert false;
//			}
			higher = endHalfface.lastSkeletonNode();
		}
//		assert !higher.isProcessed();
		return higher;
	}

	private Node getNodeFromRight(RightSplitNode rightNode) {
		Node lower = sortedLinkEnds.lower(rightNode);
		if (lower == null) {
//			assert !startHalfface.lastSkeletonNode().isProcessed();
			lower = startHalfface.lastSkeletonNode();
		}
//		assert !lower.isProcessed();
		return lower;
	}

	private double projectionOnEdge(Node node) {
		double edx = endHalfface.firstSkeletonNode().vertex.x - startHalfface.firstSkeletonNode().vertex.x;
		double edy = endHalfface.firstSkeletonNode().vertex.y - startHalfface.firstSkeletonNode().vertex.y;
		return (
			(node.vertex.x - startHalfface.firstSkeletonNode().vertex.x) * edx
				+ (node.vertex.y - startHalfface.firstSkeletonNode().vertex.y) * edy
		) / (edx * edx + edy * edy);
//		return Vector2D.fromStartToEnd(edge.start, vertex).dotProduct(edgeVector) / edgeVector.magnitude() / edgeVector.magnitude();
	}

	/**
	 * @param end1
	 * 	Order doesn't matter.
	 * @param end2
	 * 	Order doesn't matter.
	 */
	void addLink(Node end1, Node end2) {
		assert chainAtEnd1 == null && chainAtEnd2 == null;
		findChainsAtEnds(end1, end2);
		boolean hasLinkAt1 = chainAtEnd1 != null;
		boolean hasLinkAt2 = chainAtEnd2 != null;
		if (hasLinkAt1 && hasLinkAt2) {
			boolean toBeClosed = isHalfface(chainAtEnd1) && isHalfface(chainAtEnd2);
			uniteChainsWithAddedEdge();
			if (toBeClosed) {
//				closeFace();
			}
		} else if (hasLinkAt1) {
			prolongChainAtEnd1(end2);
		} else if (hasLinkAt2) {
			prolongChainAtEnd2(end1);
		} else {
			// TODO: This branch only runs when it is a split event, maybe extract it to a separate method?
			createNewLink(end1, end2);
		}
		if ((!(hasLinkAt1 && hasLinkAt2)) && !(startHalfface.firstFaceNode().getNext() == null || startHalfface
			.firstFaceNode().getPrevious() == null)) {
			assert false;
		}
		// We don't have to reset chainAtEnd[12]First
		// because they make sense and are changed only when chainAtEnd[12] is set.
		chainAtEnd1 = chainAtEnd2 = null;
	}


	private void findChainsAtEnds(Node end1, Node end2) {
		Chain chain = startHalfface;
		while (chain != null) {
			if (chainAtEnd1 == null) {
				if (chain.firstSkeletonNode() == end1 && chain.firstSkeletonNode() != chain.lastSkeletonNode()) {
					chainAtEnd1 = chain;
					linkAtEnd1First = true;
				} else if (chain.lastSkeletonNode() == end1) {
					chainAtEnd1 = chain;
					linkAtEnd1First = false;
				}
			}
			if (chainAtEnd2 == null) {
				if (chain.firstSkeletonNode() == end2 && chain.firstSkeletonNode() != chain.lastSkeletonNode()) {
					chainAtEnd2 = chain;
					linkAtEnd2First = true;
				} else if (chain.lastSkeletonNode() == end2) {
					chainAtEnd2 = chain;
					linkAtEnd2First = false;
				}
			}
			chain = chain.nextChain;
		}
	}


	private void prolongChainAtEnd1(Node end2) {
		prolongLink(end2, chainAtEnd1, linkAtEnd1First);
	}

	private void prolongLink(Node end, Chain chain, boolean isFirst) {
		numberOfSkeletonNodes++;
		if (isFirst) {
			forgetNodeProjection(chain.firstSkeletonNode());
			DoublyLinkedNode<Node> newFirst = new DoublyLinkedNode<>(end);
			DoublyLinkedNode<Node> first = chain.firstFaceNode();
			first.setPrevious(newFirst);
			newFirst.setNext(first);
			chain.moveFirstFaceNode(newFirst);
		} else {
			forgetNodeProjection(chain.lastSkeletonNode());
			DoublyLinkedNode<Node> newLast = new DoublyLinkedNode<>(end);
			DoublyLinkedNode<Node> last = chain.lastFaceNode();
			last.setNext(newLast);
			newLast.setPrevious(last);
			chain.moveLastFaceNode(newLast);
		}
		if (!isHalfface(chain)) {
			addNewSortedEnd(end);
		}
	}

	private void forgetNodeProjection(Node node) {
		sortedLinkEnds.remove(node);
	}

	private void addNewSortedEnd(Node end) {
		sortedLinkEnds.add(end);
		assert !end.vertex.equals(startHalfface.firstSkeletonNode().vertex)
			&& !end.vertex.equals(endHalfface.firstSkeletonNode().vertex);
	}

	private void prolongChainAtEnd2(Node end1) {
		prolongLink(end1, chainAtEnd2, linkAtEnd2First);
	}

	private void uniteChainsWithAddedEdge() {
		assert chainAtEnd1 != null && chainAtEnd2 != null;
		// We grow chain at end 1,
		// but if chain at end 2 is the initial left or right half-face,
		// then we grow it instead.
		// Of course there is a case when the chain at end 1 is the left (right) half-face,
		// and the chain at end 2 is the another, right (left) half-face. In that case order doesn't matter.
		boolean shouldBeSwapped = isHalfface(chainAtEnd2);
		if (shouldBeSwapped && chainAtEnd1 != startHalfface) {
			// Swap
			Chain chainBuf = chainAtEnd1;
			chainAtEnd1 = chainAtEnd2;
			chainAtEnd2 = chainBuf;
			// Swap
			boolean firstBuf = linkAtEnd1First;
			linkAtEnd1First = linkAtEnd2First;
			linkAtEnd2First = firstBuf;
		}

		if (isHalfface(chainAtEnd1)) {
			forgetNodeProjection(linkAtEnd2First ? chainAtEnd2.lastSkeletonNode() : chainAtEnd2.firstSkeletonNode());
		}
		forgetNodeProjection(linkAtEnd1First ? chainAtEnd1.firstSkeletonNode() : chainAtEnd1.lastSkeletonNode());
		forgetNodeProjection(linkAtEnd2First ? chainAtEnd2.firstSkeletonNode() : chainAtEnd2.lastSkeletonNode());

		assert chainAtEnd1 != null && chainAtEnd2 != null;
		if (linkAtEnd1First && linkAtEnd2First) {
			DoublyLinkedNode<Node> first1 = chainAtEnd1.firstFaceNode();
			DoublyLinkedNode<Node> oldFirst2 = chainAtEnd2.firstFaceNode();
			oldFirst2.revertChain();
			first1.setPrevious(oldFirst2);
			oldFirst2.setNext(first1);
			chainAtEnd1.moveFirstFaceNode(chainAtEnd2.lastFaceNode());
		} else if (linkAtEnd1First) {
			assert !linkAtEnd2First;
			DoublyLinkedNode<Node> first1 = chainAtEnd1.firstFaceNode();
			DoublyLinkedNode<Node> last2 = chainAtEnd2.lastFaceNode();
			first1.setPrevious(last2);
			last2.setNext(first1);
			chainAtEnd1.moveFirstFaceNode(chainAtEnd2.firstFaceNode());
		} else if (linkAtEnd2First) {
			assert !linkAtEnd1First;
			DoublyLinkedNode<Node> last1 = chainAtEnd1.lastFaceNode();
			DoublyLinkedNode<Node> first2 = chainAtEnd2.firstFaceNode();
			last1.setNext(first2);
			first2.setPrevious(last1);
			chainAtEnd1.moveLastFaceNode(chainAtEnd2.lastFaceNode());
		} else {
			assert !linkAtEnd1First && !linkAtEnd2First;
			DoublyLinkedNode<Node> last1 = chainAtEnd1.lastFaceNode();
			DoublyLinkedNode<Node> oldLast2 = chainAtEnd2.lastFaceNode();
			oldLast2.revertChain();
			last1.setNext(oldLast2);
			oldLast2.setPrevious(last1);
			chainAtEnd1.moveLastFaceNode(chainAtEnd2.firstFaceNode());
		}
		chainAtEnd2.removeFromFace();
		if (lastAddedChain == chainAtEnd2) {
			lastAddedChain = chainAtEnd2.previousChain;
		}
	}

	private boolean isHalfface(Chain chain) {
		return chain == startHalfface || chain == endHalfface;
	}

	private void createNewLink(Node oneEnd, Node anotherEnd) {
		assert lastAddedChain != null;
		lastAddedChain = new Chain(oneEnd, anotherEnd, lastAddedChain);
		assert lastAddedChain.previousChain != null;
		lastAddedChain.previousChain.setNextChain(lastAddedChain);
		addNewSortedEnd(oneEnd);
//		sortedLinkEnds.add(oneEnd);
		addNewSortedEnd(anotherEnd);
//		sortedLinkEnds.add(anotherEnd);
		numberOfSkeletonNodes += 2;
	}

	public Polygon toPolygon() {
		List<Point2D> points = new ArrayList<>(numberOfSkeletonNodes);
		DoublyLinkedNode<Node> chain = startHalfface.firstFaceNode();
		assert chain.isTerminal();
		Point2D previousPayload = null;
		for (Node node : chain) {
			if (node.vertex == previousPayload) {
				// This happens at split event points and at starts of half-faces
				continue;
			}
			if (!(points.size() == 0 || !node.vertex.equals(points.get(points.size() - 1)))) {
				assert false;
			}
			points.add(node.vertex);
			previousPayload = node.vertex;
		}

		assert !points.get(0).equals(points.get(points.size() - 1));
		// TODO: Remove this check
		if (JTSUtils.isYDownCCW(points)) {
			TestCanvas.canvas.drawAll(
				new Polygon(points).toSegments(),
				DrawingSegment2D.withColorDirected(Color.white, 1)
			);
			assert false;
		}
		return new Polygon(points);
	}

	boolean isClosed() {
		return startHalfface.lastFaceNode() == endHalfface.firstFaceNode()
			|| endHalfface.lastFaceNode() == startHalfface.firstFaceNode();
	}

	Segment2D findClosestIntersectedSegment(Segment2D ray) {
		assert isClosed();
		return Stream.concat(startHalfface.asSegmentStream(), endHalfface.asSegmentStream())
			.filter(s -> {
				double r = new RayIntersection(s, ray).r;
				return r < 1. && r > 0.;
			})
			.min((a, b) -> (int) Math.signum(new RayIntersection(ray, a).r - new RayIntersection(ray, b).r))
			.get();
	}

	void integrateSplitNodes(Node parent, LeftSplitNode leftNode, RightSplitNode rightNode) {
		Node leftLavNextNode, rightLavPreviousNode;
		assert !isClosed();
		leftLavNextNode = getNodeFromLeft(leftNode);
		rightLavPreviousNode = getNodeFromRight(rightNode);

		leftNode.setPreviousInLav(parent.previous());
		leftLavNextNode.setPreviousInLav(leftNode);

		rightNode.setPreviousInLav(rightLavPreviousNode);
		parent.next().setPreviousInLav(rightNode);

		parent.setProcessed();

		parent.growRightFace(rightNode);
		parent.growLeftFace(leftNode);
		addLink(leftNode, rightNode);
	}

	@Override
	public Iterator<Node> iterator() {
		assert isClosed();
		return startHalfface.firstFaceNode() == startHalfface.lastFaceNode() ?
			startHalfface.firstFaceNode().iterator()
			: endHalfface.firstFaceNode().iterator();
	}

	OriginalEdgeStart findAnotherOppositeEdgeStart(Node parent) {
		Node leftLavNextNode, rightLavPreviousNode;
		Segment2D oppositeInClosed = findClosestIntersectedSegment(parent.bisector);
		Node oneNode = null, anotherNode = null;
		for (Node node : this) {
			System.out.println(node.vertex);
			if (node.vertex.equals(oppositeInClosed.start)
				|| node.vertex.equals(oppositeInClosed.end)) {
				if (oneNode == null) {
					oneNode = node;
				} else {
					assert anotherNode == null;
					anotherNode = node;
				}
			}
		}
		assert oneNode != null && anotherNode != null;
		if (parent.bisector.isLeftOfRay(oneNode.vertex)) {
			leftLavNextNode = oneNode;
			rightLavPreviousNode = anotherNode;
		} else {
			leftLavNextNode = anotherNode;
			rightLavPreviousNode = oneNode;
		}
		while (leftLavNextNode.isProcessed()) {
			leftLavNextNode = leftLavNextNode.next();
		}
		while (rightLavPreviousNode.isProcessed()) {
			rightLavPreviousNode = rightLavPreviousNode.previous();
		}
		return leftLavNextNode.previousEdgeStart;
//			TestCanvas.canvas.draw(rightLavPreviousNode.vertex, DrawingPoint2D.withColorAndSize(Color.orange, 1));
//		TestCanvas.canvas.draw(leftLavNextNode.currentEdge, DrawingSegment2D.withColorDirected(Color.blue, 1));
//			TestCanvas.canvas.draw(rightLavPreviousNode.previousEdge(), DrawingSegment2D.withColorDirected(Color.cyan,  1));
	}
}

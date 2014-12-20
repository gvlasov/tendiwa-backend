package org.tendiwa.geometry.extensions.straightSkeleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.TreeSet;

final class Face {
	@Nonnull
	final Link startHalfface;
	@Nonnull
	final Link endHalfface;
	@Nullable
	private Link linkAtEnd1;
	@Nullable
	private Link linkAtEnd2;
	private boolean linkAtEnd1First;
	private boolean linkAtEnd2First;
	private Link lastAddedLink;
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
		startHalfface = new Link(edgeStart, edgeStart, null);
		endHalfface = new Link(edgeEnd, edgeEnd, startHalfface);
		startHalfface.setNext(endHalfface);
		assert startHalfface.nextLink == endHalfface && endHalfface.previousLink == startHalfface
			&& startHalfface.previousLink == null && endHalfface.nextLink == null;
		lastAddedLink = endHalfface;
	}

	private boolean isEndOfHalfface(Node end) {
		return end == startHalfface.last || end == endHalfface.last;
	}

	Node getNodeFromLeft(LeftSplitNode leftNode) {
		Node higher = sortedLinkEnds.higher(leftNode);
		if (higher == null) {
			assert !endHalfface.last.isProcessed();
			higher = endHalfface.last;
		}
		boolean b = !higher.isProcessed();
		if (!b) {
			assert false;
		}
		assert b;
		return higher;
	}

	Node getNodeFromRight(RightSplitNode rightNode) {
		Node lower = sortedLinkEnds.lower(rightNode);
		if (lower == null) {
			assert !startHalfface.last.isProcessed();
			lower = startHalfface.last;
		}
		boolean b = !lower.isProcessed();
		if (!b) {
			assert false;
		}
		assert b;
		return lower;
	}

	private double projectionOnEdge(Node node) {
		double edx = endHalfface.first.vertex.x - startHalfface.first.vertex.x;
		double edy = endHalfface.first.vertex.y - startHalfface.first.vertex.y;
		return (
			(node.vertex.x - startHalfface.first.vertex.x) * edx
				+ (node.vertex.y - startHalfface.first.vertex.y) * edy
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
		assert linkAtEnd1 == null && linkAtEnd2 == null;
		findLinksAtEnds(end1, end2);
		boolean hasLinkAt1 = linkAtEnd1 != null;
		boolean hasLinkAt2 = linkAtEnd2 != null;
		if (hasLinkAt1 && hasLinkAt2) {
			uniteChainsWithAddedEdge();
		} else if (hasLinkAt1) {
			prolongChainAtEnd1(end2);
		} else if (hasLinkAt2) {
			prolongChainAtEnd2(end1);
		} else {
			createNewLink(end1, end2);
		}
		// We don't have to reset chainAtEnd[12]First
		// because they make sense and are changed only when chainAtEnd[12] is set.
		linkAtEnd1 = linkAtEnd2 = null;
	}

	private void findLinksAtEnds(Node oneEnd, Node anotherEnd) {
		Link link = startHalfface;
		while (link != null) {
			if (linkAtEnd1 == null) {
				if (link.first == oneEnd && link.first != link.last) {
					linkAtEnd1 = link;
					linkAtEnd1First = true;
				} else if (link.last == oneEnd) {
					linkAtEnd1 = link;
					linkAtEnd1First = false;
				}
			}
			try {
				if (linkAtEnd2 == null) {
					if (link.first == anotherEnd) {
						linkAtEnd2 = link;
						linkAtEnd2First = true;
					} else if (link.last == anotherEnd) {
						linkAtEnd2 = link;
						linkAtEnd2First = false;
					}
				}
			} catch (NoSuchElementException e) {
				assert false;
			}
			link = link.nextLink;
		}
	}


	private void prolongChainAtEnd1(Node end2) {
		prolongLink(end2, linkAtEnd1, linkAtEnd1First);
	}

	private void prolongLink(Node end, Link link, boolean isFirst) {
		if (isFirst) {
			sortedLinkEnds.remove(link.first);
			link.moveFirst(end);
		} else {
			sortedLinkEnds.remove(link.last);
			link.moveLast(end);
		}
		if (!isHalfface(link)) {
			addNewSortedEnd(end);
		}
	}

	private void addNewSortedEnd(Node end) {
		sortedLinkEnds.add(end);
		boolean b = !end.vertex.equals(startHalfface.first.vertex)
			&& !end.vertex.equals(endHalfface.first.vertex);
		if (!b) {
			assert false;
		}
		assert b;
	}

	private void prolongChainAtEnd2(Node end1) {
		prolongLink(end1, linkAtEnd2, linkAtEnd2First);
	}

	private void uniteChainsWithAddedEdge() {
		assert linkAtEnd1 != null && linkAtEnd2 != null;
		// We grow chain at end 1,
		// but if chain at end 2 is the initial left or right half-face,
		// then we grow it instead.
		// Of course there is a case when the chain at end 1 is the left (right) half-face,
		// and the chain at end 2 is the another, right (left) half-face. In that case order doesn't matter.
		boolean shouldBeSwapped = isHalfface(linkAtEnd2);
		if (shouldBeSwapped) {
			// Swap
			Link linkBuf = linkAtEnd1;
			linkAtEnd1 = linkAtEnd2;
			linkAtEnd2 = linkBuf;
			// Swap
			boolean firstBuf = linkAtEnd1First;
			linkAtEnd1First = linkAtEnd2First;
			linkAtEnd2First = firstBuf;
		}

		if (isHalfface(linkAtEnd1)) {
			sortedLinkEnds.remove(linkAtEnd2First ? linkAtEnd2.last : linkAtEnd2.first);
		}
		sortedLinkEnds.remove(linkAtEnd1First ? linkAtEnd1.first : linkAtEnd1.last);
		sortedLinkEnds.remove(linkAtEnd2First ? linkAtEnd2.first : linkAtEnd2.last);

		assert linkAtEnd1 != null && linkAtEnd2 != null;
		if (linkAtEnd1First && linkAtEnd2First) {
			linkAtEnd1.moveFirst(linkAtEnd2.last);
		} else if (linkAtEnd1First) {
			linkAtEnd1.moveFirst(linkAtEnd2.first);
		} else if (linkAtEnd2First) {
			linkAtEnd1.moveLast(linkAtEnd2.last);
		} else {
			linkAtEnd1.moveLast(linkAtEnd2.first);
		}
		linkAtEnd2.removeFromChain();
	}

	private boolean isHalfface(Link link) {
		return link == startHalfface || link == endHalfface;
	}

	private void createNewLink(Node oneEnd, Node anotherEnd) {
		assert lastAddedLink != null;
		lastAddedLink = new Link(oneEnd, anotherEnd, lastAddedLink);
		assert lastAddedLink.previousLink != null;
		lastAddedLink.previousLink.setNext(lastAddedLink);
		sortedLinkEnds.add(oneEnd);
		sortedLinkEnds.add(anotherEnd);
	}

}

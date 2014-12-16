package org.tendiwa.geometry.extensions.straightSkeleton;

import org.jetbrains.annotations.Nullable;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.util.*;

final class Face {
	final Link startHalfface;
	final Link endHalfface;
	private Link linkAtEnd1;
	private Link linkAtEnd2;
	private boolean linkAtEnd1First;
	private boolean linkAtEnd2First;
	private Link lastAddedLink;

	Face(OriginalEdgeStart edgeStart, OriginalEdgeStart edgeEnd) {
		startHalfface = new Link(edgeStart, edgeStart, null);
		endHalfface = new Link(edgeEnd, edgeEnd, startHalfface);
		startHalfface.setNext(endHalfface);
		assert startHalfface.nextLink == endHalfface && endHalfface.previousLink == startHalfface
			&& startHalfface.previousLink == null && endHalfface.nextLink == null;
		lastAddedLink = endHalfface;
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
		if (endHalfface.first.vertex.hashCode() == -970673654) {
			TestCanvas.canvas.draw(
				new Segment2D(
					end1.vertex,
					end2.vertex
				),
				DrawingSegment2D.withColorThin(Color.magenta)
			);
			assert Boolean.TRUE;
		}
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


	private void prolongChainAtEnd1(Node end2) {
		if (linkAtEnd1First) {
			linkAtEnd1.moveFirst(end2);
		} else {
			linkAtEnd1.moveLast(end2);
		}
	}

	private void prolongChainAtEnd2(Node end1) {
		if (linkAtEnd2First) {
			linkAtEnd2.moveFirst(end1);
		} else {
			linkAtEnd2.moveLast(end1);
		}
	}

	private void uniteChainsWithAddedEdge() {
		// We grow chain at end 1,
		// but if chain at end 2 is the initial left or right half-face,
		// then we grow it instead.
		// Of course there is a case when the chain at end 1 is the left (right) half-face,
		// and the chain at end 2 is the another, right (left) half-face. In that case order doesn't matter.
		if (linkAtEnd2 == startHalfface || linkAtEnd2 == endHalfface) {
			// Swap
			Link linkBuf = linkAtEnd1;
			linkAtEnd1 = linkAtEnd2;
			linkAtEnd2 = linkBuf;
			// Swap
			boolean firstBuf = linkAtEnd1First;
			linkAtEnd1First = linkAtEnd2First;
			linkAtEnd2First = firstBuf;
		}
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

	private void createNewLink(Node oneEnd, Node anotherEnd) {
		lastAddedLink = new Link(oneEnd, anotherEnd, lastAddedLink);
		lastAddedLink.previousLink.setNext(lastAddedLink);
	}

	Node whereStartMoved() {
		return startHalfface.last;
	}

	Node whereEndMoved() {
		return endHalfface.last;
	}

	final class Link {
		Node first;
		private Node last;
		/**
		 * To iterate over all Links of this Face.
		 */
		@Nullable private Link nextLink;
		@Nullable private Link previousLink;

		private Link(Node oneEnd, Node last, @Nullable Link previousLink) {
			this.first = oneEnd;
			this.last = last;
			this.previousLink = previousLink;
		}

		private void setNext(@Nullable Link nextLink) {
			this.nextLink = nextLink;
		}

		private void moveFirst(Node newFirst) {
			this.first = newFirst;
		}

		private void moveLast(Node newLast) {
			this.last = newLast;
		}

		public void removeFromChain() {
			if (nextLink != null) {
				nextLink.previousLink = previousLink;
			}
			if (previousLink != null) {
				previousLink.setNext(nextLink);
			}
		}
	}
}

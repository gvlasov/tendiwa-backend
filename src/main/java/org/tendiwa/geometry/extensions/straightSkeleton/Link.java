package org.tendiwa.geometry.extensions.straightSkeleton;

import org.jetbrains.annotations.Nullable;

final class Link {
	Node first;
	Node last;
	/**
	 * To iterate over all Links of this Face.
	 */
	@Nullable
	Link nextLink;
	@Nullable
	Link previousLink;

	Link(Node oneEnd, Node last, @Nullable Link previousLink) {
		this.first = oneEnd;
		this.last = last;
		this.previousLink = previousLink;
	}

	void setNext(@Nullable Link nextLink) {
		this.nextLink = nextLink;
	}

	void moveFirst(Node newFirst) {
		this.first = newFirst;
	}

	void moveLast(Node newLast) {
		this.last = newLast;
	}

	void removeFromChain() {
		if (nextLink != null) {
			nextLink.previousLink = previousLink;
		}
		if (previousLink != null) {
			previousLink.setNext(nextLink);
		}
	}
}

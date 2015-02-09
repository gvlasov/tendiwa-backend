package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.collections.DoublyLinkedNode;

import javax.annotation.Nullable;

/**
 * Holds start and end of a subchain of a {@link Face}
 */
final class Chain {
	private DoublyLinkedNode<Node> first;
	private DoublyLinkedNode<Node> last;
	/**
	 * To iterate over all Links of this Face.
	 */
	@Nullable
	Chain nextChain;
	@Nullable
	Chain previousChain;

	Chain(Node oneEnd, Node last, @Nullable Chain previousChain) {
		this.first = new DoublyLinkedNode<>(oneEnd);
		this.last = new DoublyLinkedNode<>(last);
		this.first.setNext(this.last);
		this.last.setPrevious(this.first);

		this.previousChain = previousChain;
	}

	Node firstSkeletonNode() {
		return first.getPayload();
	}

	Node lastSkeletonNode() {
		return last.getPayload();
	}

	DoublyLinkedNode<Node> firstFaceNode() {
		return first;
	}

	DoublyLinkedNode<Node> lastFaceNode() {
		return last;
	}

	void setNextChain(@Nullable Chain nextChain) {
		this.nextChain = nextChain;
	}

	void moveFirstFaceNode(DoublyLinkedNode<Node> newFirst) {
		first = newFirst;
	}

	void moveLastFaceNode(DoublyLinkedNode<Node> newLast) {
		last = newLast;
	}

	void removeFromFace() {
		if (nextChain != null) {
			nextChain.previousChain = previousChain;
		}
		if (previousChain != null) {
			previousChain.setNextChain(nextChain);
		}
	}
}

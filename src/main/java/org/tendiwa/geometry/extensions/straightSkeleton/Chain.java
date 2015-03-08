package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.collections.DoublyLinkedNode;

import javax.annotation.Nullable;

/**
 * Holds start and end of a subchain of a {@link IncompleteFace}
 */
final class Chain {
	private DoublyLinkedNode<Node> first;
	private DoublyLinkedNode<Node> last;
	@Nullable
	Chain nextChain;
	@Nullable
	Chain previousChain;

	Chain(Node oneEnd, Node last, @Nullable Chain previousChain) {
		if (oneEnd == last) {
			this.first = new DoublyLinkedNode<>(oneEnd);
			this.last = this.first;
		} else {
			this.first = new DoublyLinkedNode<>(oneEnd);
			this.last = new DoublyLinkedNode<>(last);
			this.first.setNext(this.last);
			this.last.setPrevious(this.first);
		}

		this.previousChain = previousChain;
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

	boolean isZeroLength() {
		return firstFaceNode().getPayload() == lastFaceNode().getPayload();
	}
}

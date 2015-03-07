package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.Segment2D;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Holds start and end of a subchain of a {@link IncompleteFace}
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


	void skipFirst() {
		this.first = first.getNext();
	}

	void skipLast() {
		this.last = last.getPrevious();
	}

	int size() {
		return Lists.newArrayList(first.iterator()).size();
	}
}

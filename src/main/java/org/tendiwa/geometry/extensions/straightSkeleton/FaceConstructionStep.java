package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.collections.DoublyLinkedNode;

import javax.annotation.Nullable;

final class FaceConstructionStep {
	private final UnderlyingFace face;
	private final Node end1;
	private final Node end2;
	@Nullable
	private final Chain chainAtEnd1;
	@Nullable
	private final Chain chainAtEnd2;
	private final boolean linkAtEnd1First;
	private final boolean linkAtEnd2First;

	FaceConstructionStep(UnderlyingFace face, Node end1, Node end2) {
		this.face = face;
		this.end1 = end1;
		this.end2 = end2;
		Chain chain = face.startHalfface();
		Chain chainAtEnd1 = null, chainAtEnd2 = null;
		boolean linkAtEnd1First = false,
			linkAtEnd2First = false;
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
		this.chainAtEnd1 = chainAtEnd1;
		this.chainAtEnd2 = chainAtEnd2;
		this.linkAtEnd1First = linkAtEnd1First;
		this.linkAtEnd2First = linkAtEnd2First;
	}

	void run() {
		boolean hasLinkAt1 = chainAtEnd1 != null;
		boolean hasLinkAt2 = chainAtEnd2 != null;
		if (hasLinkAt1 && hasLinkAt2) {
			uniteChainsWithAddedEdge();
		} else if (hasLinkAt1) {
			prolongChainAtEnd1(end2);
		} else if (hasLinkAt2) {
			prolongChainAtEnd2(end1);
		} else {
			// TODO: This branch only runs when it is a split event, maybe extract it to a separate method?
			createNewLink(end1, end2);
		}
		if (
			(!(hasLinkAt1 && hasLinkAt2))
				&& !(face.startHalfface().firstFaceNode().getNext() == null
				|| face .startHalfface() .firstFaceNode().getPrevious() == null)) {
			assert false;
		}
	}

	private void createNewLink(Node oneEnd, Node anotherEnd) {
		assert face.lastAddedChain() != null;
		face.setLastAddedChain(new Chain(oneEnd, anotherEnd, face.lastAddedChain()));
		assert face.lastAddedChain().previousChain != null;
		face.lastAddedChain().previousChain.setNextChain(face.lastAddedChain());
		face.addNewSortedEnd(oneEnd);
//		sortedLinkEnds.add(oneEnd);
		face.addNewSortedEnd(anotherEnd);
//		sortedLinkEnds.add(anotherEnd);
		face.increaseNumberOfSkeletonNodes(2);
	}


	private void prolongChainAtEnd1(Node end2) {
		prolongLink(end2, chainAtEnd1, linkAtEnd1First);
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
		boolean shouldBeSwapped = face.isHalfface(chainAtEnd2);
		Chain chainAtEnd1 = this.chainAtEnd1;
		Chain chainAtEnd2 = this.chainAtEnd2;
		boolean linkAtEnd1First = this.linkAtEnd1First;
		boolean linkAtEnd2First = this.linkAtEnd2First;
		if (shouldBeSwapped && chainAtEnd1 != face.startHalfface()) {
			// Swap
			Chain chainBuf = chainAtEnd1;
			chainAtEnd1 = chainAtEnd2;
			chainAtEnd2 = chainBuf;
			// Swap
			boolean firstBuf = linkAtEnd1First;
			linkAtEnd1First = linkAtEnd2First;
			linkAtEnd2First = firstBuf;
		}

		if (face.isHalfface(chainAtEnd1)) {
			face.forgetNodeProjection(linkAtEnd2First ? chainAtEnd2.lastSkeletonNode() : chainAtEnd2.firstSkeletonNode
				());
		}
		face.forgetNodeProjection(linkAtEnd1First ? chainAtEnd1.firstSkeletonNode() : chainAtEnd1.lastSkeletonNode());
		face.forgetNodeProjection(linkAtEnd2First ? chainAtEnd2.firstSkeletonNode() : chainAtEnd2.lastSkeletonNode());

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
		if (face.lastAddedChain() == chainAtEnd2) {
			face.setLastAddedChain(chainAtEnd2.previousChain);
		}
	}

	private void prolongLink(Node end, Chain chain, boolean isFirst) {
		face.increaseNumberOfSkeletonNodes(1);
		if (isFirst) {
			face.forgetNodeProjection(chain.firstSkeletonNode());
			DoublyLinkedNode<Node> newFirst = new DoublyLinkedNode<>(end);
			DoublyLinkedNode<Node> first = chain.firstFaceNode();
			first.setPrevious(newFirst);
			newFirst.setNext(first);
			chain.moveFirstFaceNode(newFirst);
		} else {
			face.forgetNodeProjection(chain.lastSkeletonNode());
			DoublyLinkedNode<Node> newLast = new DoublyLinkedNode<>(end);
			DoublyLinkedNode<Node> last = chain.lastFaceNode();
			last.setNext(newLast);
			newLast.setPrevious(last);
			chain.moveLastFaceNode(newLast);
		}
		if (!face.isHalfface(chain)) {
			face.addNewSortedEnd(end);
		}
	}
}

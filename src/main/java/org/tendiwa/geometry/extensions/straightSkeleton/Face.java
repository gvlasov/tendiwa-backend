package org.tendiwa.geometry.extensions.straightSkeleton;

import java.util.*;

final class Face {
	final Deque<Node> startHalfface = new LinkedList<>();
	final Deque<Node> endHalfface = new LinkedList<>();
	private final List<Deque<Node>> chains = new ArrayList<>();
	private Deque<Node> chainAtEnd1;
	private Deque<Node> chainAtEnd2;
	private boolean chainAtEnd1First;
	private boolean chainAtEnd2First;

	Face(InitialNode edgeStart, InitialNode edgeEnd) {
		startHalfface.add(edgeStart);
		endHalfface.add(edgeEnd);
		chains.add(startHalfface);
		chains.add(endHalfface);
	}

	void growStartHalfface(Node node) {
//		grow(startHalfface, node);
		addEdge(startHalfface.getLast(), node);
	}

	void growEndHalfface(Node node) {
//		grow(endHalfface, node);
		addEdge(endHalfface.getLast(), node);
	}

	void grow(Deque<Node> halfface, Node node) {
		halfface.add(node);
	}

	/**
	 * @param end1
	 * 	Order doesn't matter.
	 * @param end2
	 * 	Order doesn't matter.
	 */
	void addEdge(Node end1, Node end2) {
		assert chainAtEnd1 == null && chainAtEnd2 == null;
		findChainsAtEnds(end1, end2);
		boolean hasChainAt1 = chainAtEnd1 != null;
		boolean hasChainAt2 = chainAtEnd2 != null;
		if (hasChainAt1 && hasChainAt2) {
			uniteChainsWithAddedEdge();
		} else if (hasChainAt1) {
			prolongChainAtEnd1(end2);
		} else if (hasChainAt2) {
			prolongChainAtEnd2(end1);
		} else {
			createNewChain(end1, end2);
		}
		// We don't have to reset chainAtEnd[12]First
		// because they make sense and are changed only when chainAtEnd[12] is set.
		chainAtEnd1 = chainAtEnd2 = null;
	}


	private void prolongChainAtEnd1(Node end2) {
		if (chainAtEnd1First) {
			chainAtEnd1.addFirst(end2);
		} else {
			chainAtEnd1.addLast(end2);
		}
	}

	private void prolongChainAtEnd2(Node end1) {
		if (chainAtEnd2First) {
			chainAtEnd2.addFirst(end1);
		} else {
			chainAtEnd2.addLast(end1);
		}
	}

	private void uniteChainsWithAddedEdge() {
		// We grow chain at end 1,
		// but if chain at end 2 is the initial left or right half-face,
		// then we grow it instead.
		// Of course there is a case when the chain at end 1 is the left (right) half-face,
		// and the chain at end 2 is the right (left) half-face. In that case order doesn't matter.
		if (chainAtEnd2 == startHalfface || chainAtEnd2 == endHalfface) {
			Deque<Node> buf = chainAtEnd1;
			chainAtEnd1 = chainAtEnd2;
			chainAtEnd2 = buf;
		}
		if (chainAtEnd1First && chainAtEnd2First) {
			while (!chainAtEnd2.isEmpty()) {
				chainAtEnd1.addFirst(chainAtEnd2.pollFirst());
			}
		} else if (chainAtEnd1First) {
			while (!chainAtEnd2.isEmpty()) {
				chainAtEnd1.addFirst(chainAtEnd2.pollLast());
			}
		} else if (chainAtEnd2First) {
			while (!chainAtEnd2.isEmpty()) {
				chainAtEnd1.addLast(chainAtEnd2.pollFirst());
			}
		} else {
			while (!chainAtEnd2.isEmpty()) {
				chainAtEnd1.addLast(chainAtEnd2.pollLast());
			}
		}
		chains.remove(chainAtEnd2);
	}

	private void findChainsAtEnds(Node oneEnd, Node anotherEnd) {
		for (Deque<Node> chain : chains) {
			if (chainAtEnd1 == null) {
				if (chain.getFirst() == oneEnd && chain.size() > 1) {
					chainAtEnd1 = chain;
					chainAtEnd1First = true;
				} else if (chain.getLast() == oneEnd) {
					chainAtEnd1 = chain;
					chainAtEnd1First = false;
				}
			}
			try {
				if (chainAtEnd2 == null) {
					if (chain.getFirst() == anotherEnd) {
						chainAtEnd2 = chain;
						chainAtEnd2First = true;
					} else if (chain.getLast() == anotherEnd) {
						chainAtEnd2 = chain;
						chainAtEnd2First = false;
					}
				}
			} catch (NoSuchElementException e) {
				assert false;
			}
		}
	}

	private void createNewChain(Node oneEnd, Node anotherEnd) {
		LinkedList<Node> newChain = new LinkedList<>();
		newChain.add(oneEnd);
		newChain.add(anotherEnd);
		chains.add(newChain);
	}
}

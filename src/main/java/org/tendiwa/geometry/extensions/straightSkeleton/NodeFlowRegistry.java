package org.tendiwa.geometry.extensions.straightSkeleton;


import java.util.*;

/**
 * [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
 * <p>
 * Tracks how nodes move as edges collapse. When an edge collapses, two nodes that were its ends turn into one that
 * is the point where the edge collapses.
 */
class NodeFlowRegistry {
	private final Map<Node, NodeDrain> drains = new HashMap<>();
	private final Map<Node, NodeFlow> chainByTail;

	NodeFlowRegistry(List<Node> tails) {
		chainByTail = new HashMap<>(tails.size());
		for (Node node : tails) {
			NodeFlow movement = new NodeFlow(node);
			drains.put(node, new NodeDrain(movement));
			chainByTail.put(node, movement);
		}
	}

	/**
	 * Returns the movement that starts at a specific initial node.
	 *
	 * @param tail
	 * 	A node that was constructed at {@link InitialListOfActiveVertices}
	 * @return Movement whose tail is the specified node.
	 */
	NodeFlow getChainByTail(Node tail) {
		assert chainByTail.containsKey(tail);
		return chainByTail.get(tail);
	}

	/**
	 * Several movements may share heads, and any of those movements may be returned, it should not make any
	 * difference.
	 *
	 * @param head
	 * @return
	 */
	NodeFlow getChainByHead(Node head) {
		try {
			return drains.get(head).getInitialThread();
		}catch (NullPointerException e) {
			throw new RuntimeException();
		}
	}

	void move(Node oldHead, Node newHead) {
		NodeDrain atCurrentHead = drains.get(oldHead);
		if (drains.containsKey(newHead)) {
			drains.get(newHead).combineWith(atCurrentHead);
		} else {
			drains.put(newHead, atCurrentHead);
		}
		drains.remove(oldHead);
		atCurrentHead.moveHeadsTo(newHead);
	}

	void split(Node oldHead, Node newLeftHead, Node newRightHead) {
		NodeDrain rightDrain = new NodeDrain(drains.get(oldHead));
		move(oldHead, newLeftHead);
		drains.put(oldHead, rightDrain);
		move(oldHead, newRightHead);
	}

	/**
	 * Combination of multiple (1 or more) {@link NodeFlow}s with distinct tails that share their head.
	 */
	private final class NodeDrain {
		private final Deque<NodeFlow> movements = new LinkedList<>();

		NodeDrain(NodeFlow movement) {
			addTail(movement);
		}
		NodeDrain(NodeDrain drain) {
			movements.addAll(drain.movements);
		}

		void addTail(NodeFlow movement) {
			movements.add(movement);
			assert movements.stream().allMatch(tail -> tail.getHead() == movement.getHead());
		}

		void moveHeadsTo(Node newHead) {
			movements.forEach(leaf -> leaf.changeHead(newHead));
		}

		void combineWith(NodeDrain source) {
			try {
				movements.addAll(source.movements);
			}catch (RuntimeException e) {
				assert false;
			}
		}


		NodeFlow getInitialThread() {
			return movements.getFirst();
		}
	}
}

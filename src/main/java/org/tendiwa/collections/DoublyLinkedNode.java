package org.tendiwa.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementation of a doubly linked list's node.
 *
 * @param <T>
 * 	Type of payload.
 */
public class DoublyLinkedNode<T> implements Iterable<T> {
	private final T payload;


	private DoublyLinkedNode<T> next;
	private DoublyLinkedNode<T> previous;

	public DoublyLinkedNode(T payload) {
		this.payload = payload;
	}

	public final T getPayload() {
		return payload;
	}

	public final DoublyLinkedNode<T> getNext() {
		return next;
	}

	public final DoublyLinkedNode<T> getPrevious() {
		return previous;
	}

	// TODO: Make connectWithNext and connectWithPrevious set previous and next for another node as well
	public final void connectWithNext(DoublyLinkedNode<T> node) {
		Objects.requireNonNull(node);
		assert settingNextPreservesConnectivity(node);
		next = node;
		node.previous = this;
	}

	public final void connectWithPrevious(DoublyLinkedNode<T> node) {
		Objects.requireNonNull(node);
		assert settingPreviousPreservesConnectivity(node);
		previous = node;
		node.next = this;
	}

	/**
	 * @param node
	 * 	A node to insert.
	 * @return false if inserting {@code node} with {@link #connectWithNext(DoublyLinkedNode)} will introduce a loop or
	 * isolate some part of the list.
	 * @see #settingPreviousPreservesConnectivity(DoublyLinkedNode)
	 */
	private boolean settingNextPreservesConnectivity(DoublyLinkedNode<T> node) {
		return node != null
			&& (node.previous == this || node.previous == null)
//			&& next == null
			&& node != this;
	}

	/**
	 * @param node
	 * 	A node to insert.
	 * @return false if inserting {@code node} with {@link #connectWithPrevious(DoublyLinkedNode)} will introduce a loop
	 * or
	 * isolate some part of the list.
	 */
	private boolean settingPreviousPreservesConnectivity(DoublyLinkedNode<T> node) {
		return node != null
			&& (node.next == this || node.next == null)
//			&& previous == null
			&& node != this;
	}

	/**
	 * <a href="http://www.geeksforgeeks.org/reverse-a-doubly-linked-list/">Reverts a doubly linked list</a>
	 * <p>
	 * Swaps {@link #next} and {@link #previous} of each payload in the chain of this node. This node must be either
	 * the first in the chain or the last.
	 */
	// TODO: Do we really need this method anywhere?
	public final void revertChain() {
		assert next == null || previous == null;
		DoublyLinkedNode<T> temp;
		DoublyLinkedNode<T> current = this;
		if (next == null) {
			// Starting from the last node
			while (current != null) {
				temp = current.next;
				current.next = current.previous;
				current.previous = temp;
				current = current.next;
			}
		} else {
			// Starting from the first node
			while (current != null) {
				temp = current.previous;
				current.previous = current.next;
				current.next = temp;
				current = current.previous;
			}
		}
	}

	/**
	 * @param listEnd
	 * 	Head or tail of another list.
	 */
	public final void uniteWith(DoublyLinkedNode<T> listEnd) {
		boolean thisNextNull = this.next == null;
		boolean thisPreviousNull = this.previous == null;
		boolean anotherNextNull = listEnd.next == null;
		boolean anotherPreviousNull = listEnd.previous == null;
		assert (thisNextNull || thisPreviousNull)
			&& (anotherNextNull || anotherPreviousNull);
		if (thisNextNull && thisPreviousNull) {
			// Two free on this end, one free on listEnd.
			if (anotherNextNull) {
				listEnd.connectWithNext(this);
				this.connectWithPrevious(listEnd);
			} else {
				assert anotherPreviousNull;
				listEnd.connectWithPrevious(this);
				this.connectWithNext(listEnd);
			}
		} else if (anotherNextNull && anotherPreviousNull) {
			// Two free on listEnd, one free on this end.
			if (thisNextNull) {
				this.connectWithNext(listEnd);
				listEnd.connectWithPrevious(this);
			} else {
				assert thisPreviousNull;
				this.connectWithPrevious(listEnd);
				listEnd.connectWithNext(this);
			}
		} else {
			// One free on one end, one free on another end.
			assert thisPreviousNull ^ thisNextNull;
			assert anotherPreviousNull ^ anotherNextNull;
			if (previous == null && anotherNextNull) {
				listEnd.next = this;
				previous = listEnd;
			} else if (next == null && anotherPreviousNull) {
				listEnd.previous = this;
				next = listEnd;
			} else {
				assert anotherNextNull && thisNextNull
					|| anotherPreviousNull && thisPreviousNull;
				listEnd.revertChain();
				uniteWith(listEnd);
			}
		}
	}

	// TODO: Split into chain iterator and cycle iterator
	@Override
	public final Iterator<T> iterator() {
		return next == null ? new BackwardIterator() : new ForwardIterator();
	}

	@Override
	public final void forEach(Consumer<? super T> action) {
		if (next == null) {
			iterateChainFromEnd(action);
		} else if (previous == null) {
			iterateChainFromStart(action);
		} else {
			iterateCircularList(action);
		}
	}

	private void iterateCircularList(Consumer<? super T> action) {
		assert next != null && previous != null;
		DoublyLinkedNode<T> current = this;
		do {
			action.accept(current.payload);
			current = current.next;
			if (current == null) {
				throw new IllegalArgumentException(
					"You can only iterate over a chain of nodes beginning from a " +
						"node that is either end or start of a chain, or is in the middle of a circular chain"
				);
			}
		} while (current != this);
	}

	private void iterateChainFromStart(Consumer<? super T> action) {
		assert previous == null && next != null;
		DoublyLinkedNode<T> current = this;
		while (current != null) {
			action.accept(current.payload);
			current = current.next;
		}
	}

	private void iterateChainFromEnd(Consumer<? super T> action) {
		assert next == null && previous != null;
		DoublyLinkedNode<T> current = this;
		while (current != null) {
			action.accept(current.payload);
			current = current.previous;
		}
	}

	public final boolean hasBothNeighbors() {
		// TODO: Maybe here should be XOR?
		return previous != null && next != null;
	}

	// TODO: Find out if getNext is used instead of this method anywhere
	public final boolean hasNext() {
		return next != null;
	}

	public final boolean hasPrevious() {
		return previous != null;
	}

	public final boolean isStartOfAChain() {
		return hasNext() && !hasPrevious();
	}

	private abstract class LinkedListIterator implements Iterator<T> {

		protected DoublyLinkedNode<T> current = DoublyLinkedNode.this;
		protected final DoublyLinkedNode<T> start = DoublyLinkedNode.this;
		protected boolean hasNext = true;

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		protected void recomputeHasNext() {
			hasNext = current != null && current != start;
		}
		@Override
		public T next() {
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			T answer = current.payload;
			current = chooseNext(current);
			recomputeHasNext();
			return answer;
		}
		protected abstract DoublyLinkedNode<T> chooseNext(DoublyLinkedNode<T> current);
	}

	private final class ForwardIterator extends LinkedListIterator {

		@Override
		protected DoublyLinkedNode<T> chooseNext(DoublyLinkedNode<T> current) {
			return current.next;
		}
	}

	private class BackwardIterator extends LinkedListIterator {

		@Override
		protected DoublyLinkedNode<T> chooseNext(DoublyLinkedNode<T> current) {
			return current.previous;
		}
	}

	public final boolean isDisconnected() {
		return next == null && previous == null;
	}

	@Override
	public String toString() {
		return "DoublyLinkedNode{" +
			"payload=" + payload +
			'}';
	}
}

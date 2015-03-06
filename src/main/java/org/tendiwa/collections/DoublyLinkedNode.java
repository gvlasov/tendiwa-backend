package org.tendiwa.collections;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Implementation of a doubly linked list's node.
 *
 * @param <T>
 * 	Type of payload.
 */
public final class DoublyLinkedNode<T> implements Iterable<T> {
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

	public final void setNext(DoublyLinkedNode<T> node) {
		assert settingNextPreservesConnectivity(node);
		next = node;
	}

	/**
	 * @param node
	 * 	A node to insert.
	 * @return false if inserting {@code node} with {@link #setNext(DoublyLinkedNode)} will introduce a loop or
	 * isolate some part of the list.
	 * @see #settingPreviousPreservesConnectivity(DoublyLinkedNode)
	 */
	public final boolean settingNextPreservesConnectivity(DoublyLinkedNode<T> node) {
		return (node.previous == this || node.previous == null)
			&& node != null
			&& next == null
			&& node != this;
	}

	public final void setPrevious(DoublyLinkedNode<T> node) {
		assert settingPreviousPreservesConnectivity(node);
		previous = node;
	}

	/**
	 * @param node
	 * 	A node to insert.
	 * @return false if inserting {@code node} with {@link #setPrevious(DoublyLinkedNode)} will introduce a loop or
	 * isolate some part of the list.
	 * @see #settingNextPreservesConnectivity(DoublyLinkedNode)
	 */
	public boolean settingPreviousPreservesConnectivity(DoublyLinkedNode<T> node) {
		return (node.next == this || node.next == null)
			&& node != null
			&& previous == null
			&& node != this;
	}

	/**
	 * <a href="http://www.geeksforgeeks.org/reverse-a-doubly-linked-list/">Reverts a doubly linked list</a>
	 * <p>
	 * Swaps {@link #next} and {@link #previous} of each payload in the chain of this node. This node must be either
	 * the first in the chain or the last.
	 */
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
				listEnd.setNext(this);
				this.setPrevious(listEnd);
			} else {
				assert anotherPreviousNull;
				listEnd.setPrevious(this);
				this.setNext(listEnd);
			}
		} else if (anotherNextNull && anotherPreviousNull) {
			// Two free on listEnd, one free on this end.
			if (thisNextNull) {
				this.setNext(listEnd);
				listEnd.setPrevious(this);
			} else {
				assert thisPreviousNull;
				this.setPrevious(listEnd);
				listEnd.setNext(this);
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

	@Override
	public final Iterator<T> iterator() {
		return next == null ? new BackwardIterator() : new ForwardIterator();
	}

	@Override
	public final void forEach(Consumer<? super T> action) {
		if (next == null) {
			// List with start and end, starting from the end.
			DoublyLinkedNode<T> current = this;
			while (current != null) {
				action.accept(current.payload);
				current = current.previous;
			}
		} else if (previous == null) {
			// List with start and end, starting from the beginning.
			DoublyLinkedNode<T> current = this;
			while (current != null) {
				action.accept(current.payload);
				current = current.next;
			}
		} else {
			// Circular list
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
	}

	public boolean isTerminal() {
		return previous == null || next == null;
	}


	private class ForwardIterator implements Iterator<T> {
		private DoublyLinkedNode<T> current = DoublyLinkedNode.this;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public T next() {
			T answer = current.payload;
			current = current.next;
			return answer;
		}
	}

	private class BackwardIterator implements Iterator<T> {
		private DoublyLinkedNode<T> next = DoublyLinkedNode.this;

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public T next() {
			T answer = next.payload;
			next = next.previous;
			return answer;
		}
	}
}

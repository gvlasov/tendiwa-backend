package org.tendiwa.collections;

import java.util.Iterator;

public final class IntDoublyLinkedList implements Iterable<IntDoublyLinkedList.Node> {
	private Node head;
	private Node tail;

	public void add(int value) {
		if (head == null) {
			head = tail = new Node(value);
		} else {
			head.next = new Node(value);
			head.next.previous = head;
			head = head.next;
		}
	}

	@Override
	public Iterator<Node> iterator() {
		Node pretail = new Node(0);
		pretail.next = tail;
		return new Iterator<Node>() {
			private Node currentNode = pretail;

			@Override
			public boolean hasNext() {
				return currentNode.next != null;
			}

			@Override
			public void remove() {
				if (currentNode == tail && currentNode == head) {
					tail = head = null;
					pretail.next = null;
					currentNode = pretail;
				} else if (currentNode == tail) {
					currentNode.next.previous = null;
					pretail.next = currentNode.next;
					tail = currentNode.next;
					currentNode = pretail;
				} else if (currentNode == head) {
					currentNode.previous.next = null;
					currentNode = currentNode.previous;
					head = currentNode;
				} else {
					currentNode.next.previous = currentNode.previous;
					currentNode.previous.next = currentNode.next;
					currentNode = currentNode.previous;
				}
			}

			@Override
			public Node next() {
				currentNode = currentNode.next;
				return currentNode;
			}
		};
	}

	public final class Node {
		private Node previous;
		private Node next;
		public final int value;

		public Node(int value) {
			this.value = value;
		}
	}
}

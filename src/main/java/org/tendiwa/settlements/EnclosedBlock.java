package org.tendiwa.settlements;

import org.tendiwa.geometry.Point2D;

import java.util.*;

public class EnclosedBlock implements Iterable<Point2D> {
	protected final Node startNode;
	protected final int numberOfNodes;

	public EnclosedBlock(List<Point2D> outline) {
		this.numberOfNodes = outline.size();
		this.startNode = constructChainOfNodes(outline);
	}

	public List<Point2D> toPolygon() {
		Node node = startNode;
		List<Point2D> answer = new LinkedList<>();
		do {
			answer.add(node.point);
			node = node.next;
		} while (node != startNode);
		return answer;
	}

	EnclosedBlock(Node startNode) {
		assert startNode != null;
		this.startNode = startNode;
		numberOfNodes = Integer.MAX_VALUE; // With this constructor, we don't care how much segments we have.
	}

	private Node constructChainOfNodes(List<Point2D> outline) {
		Node outlineStart = new Node(outline.get(0));
		Node last = outlineStart;
		for (int i = 1; i < numberOfNodes; i++) {
			last.next = new Node(outline.get(i));
			last = last.next;
		}
		last.next = outlineStart;
		return outlineStart;
	}

	@Override
	public Iterator<Point2D> iterator() {
		return new Iterator<Point2D>() {
			Node node = startNode;
			int i = 0;

			@Override
			public boolean hasNext() {
				return node != startNode || i == 0;
			}

			@Override
			public Point2D next() {
				if (++i > numberOfNodes) {
					throw new RuntimeException("Too many iterations (" + i + ")");
				}
				Node answer = node;
				node = node.next;
				return answer.point;
			}
		};
	}


	class Node {
		// This class is made generic so subclasses
		final Point2D point;
		Node next;

		Node(Point2D point) {
			assert point != null;
			this.point = point;
		}

		void setNext(Node next) {
			assert next != null;
			assert !this.point.equals(next.point);
			this.next = next;
		}

		/**
		 * Inserts a new node after this one, connecting the new node with this node and current next node.
		 *
		 * @param coordinate
		 * 	A coordinate of a node to insert.
		 * @return The new inserted node.
		 */
		public Node insert(Point2D coordinate) {
			Node newNode = new Node(coordinate);
			newNode.setNext(this.next);
			setNext(newNode);
			return newNode;
		}
	}
}

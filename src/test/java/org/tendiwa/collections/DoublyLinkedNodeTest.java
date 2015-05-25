package org.tendiwa.collections;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class DoublyLinkedNodeTest {
	private static class ThreeNodeCycle extends DoublyLinkedNode<Point2D> {

		public ThreeNodeCycle() {
			super(point2D(1, 2));
			DoublyLinkedNode<Point2D> two = new DoublyLinkedNode<>(point2D(2, 3));
			DoublyLinkedNode<Point2D> three = new DoublyLinkedNode<>(point2D(3, 4));
			this.connectWithNext(two);
			two.connectWithNext(three);
			three.connectWithNext(this);
		}
	}

	private static class ThreeNodeChain extends DoublyLinkedNode<Point2D> {

		public ThreeNodeChain() {
			super(point2D(1, 2));
			DoublyLinkedNode<Point2D> two = new DoublyLinkedNode<>(point2D(2, 3));
			DoublyLinkedNode<Point2D> three = new DoublyLinkedNode<>(point2D(3, 4));
			this.connectWithNext(two);
			two.connectWithNext(three);
		}
	}

	@Test
	public void circular_list_for_each_touches_each_node_once() {
		DoublyLinkedNode<Point2D> cycle = new ThreeNodeCycle();
		AtomicInteger i = new AtomicInteger(0);
		cycle.forEach(p -> i.incrementAndGet());
		assertEquals(
			3,
			i.get()
		);
	}

	@Test
	public void circular_list_iterates() {
		DoublyLinkedNode<Point2D> cycle = new ThreeNodeCycle();
		Iterator<Point2D> iterator = cycle.iterator();
		for (int i = 0; i < 3; i++) {
			iterator.next();
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void circular_list_iterates_finitely() {
		DoublyLinkedNode<Point2D> cycle = new ThreeNodeCycle();
		Iterator<Point2D> iterator = cycle.iterator();
		for (int i = 0; i < 4; i++) {
			iterator.next();
		}
	}

	@Test
	public void forward_iteration_traverses_whole_chain() {
		DoublyLinkedNode<Point2D> chain = new ThreeNodeChain();
		assertNull(chain.getPrevious());
		assertNotNull(chain.getNext());
		assertEquals(
			3,
			Iterables.size(chain)
		);
	}

	@Test
	public void backward_iteration_traverses_whole_chain() {
		DoublyLinkedNode<Point2D> backwardChain = new ThreeNodeChain().getNext().getNext();
		assertNull(backwardChain.getNext());
		assertNotNull(backwardChain.getPrevious());
		assertEquals(
			3,
			Iterables.size(backwardChain)
		);
	}
}
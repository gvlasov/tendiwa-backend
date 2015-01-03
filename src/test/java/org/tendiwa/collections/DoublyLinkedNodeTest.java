package org.tendiwa.collections;

import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;

import java.util.concurrent.atomic.AtomicInteger;

public class DoublyLinkedNodeTest {
	@Test
	public void circularListForEach() {
		DoublyLinkedNode<Point2D> one = new DoublyLinkedNode<>(new Point2D(1,2));
		DoublyLinkedNode<Point2D> two = new DoublyLinkedNode<>(new Point2D(2,3));
		DoublyLinkedNode<Point2D> three = new DoublyLinkedNode<>(new Point2D(3,4));

		one.setNext(two);
		two.setNext(three);
		three.setNext(one);
		one.setPrevious(three);
		two.setPrevious(one);
		three.setPrevious(two);

		AtomicInteger i = new AtomicInteger(0);
		three.forEach(p->i.incrementAndGet());
		Assert.assertEquals(3, i.get());
	}

}
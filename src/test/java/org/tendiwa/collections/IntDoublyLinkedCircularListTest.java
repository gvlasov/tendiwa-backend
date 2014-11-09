package org.tendiwa.collections;

import org.junit.Test;

import java.util.Iterator;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class IntDoublyLinkedCircularListTest {
	/**
	 * From a list of numbers [0;9] removes numbers 0, 3, 7, 8 and 9, and checks that the numbers that are left are
	 * 1, 2, 4, 5 and 6.
	 */
	@Test
	public void removing() {
		IntDoublyLinkedList list = new IntDoublyLinkedList();
		IntStream.range(0, 10).forEach(list::add);
		Iterator<IntDoublyLinkedList.Node> iter = list.iterator();
		while (iter.hasNext()) {
			IntDoublyLinkedList.Node node = iter.next();
			if (node.value == 0 || node.value == 3 || node.value == 7 || node.value == 8 || node.value == 9) {
				iter.remove();
			}
		}
		int[] array = new int[5];
		int i = 0;
		for (IntDoublyLinkedList.Node node : list) {
			array[i++] = node.value;
		}
		assertArrayEquals(
			new int[]{1, 2, 4, 5, 6},
			array
		);

	}

	@Test
	public void removingAllFromBeginning() {
		IntDoublyLinkedList list = new IntDoublyLinkedList();
		IntStream.range(0, 10).forEach(list::add);
		Iterator<IntDoublyLinkedList.Node> iter = list.iterator();
		int i = 0;
		while (iter.hasNext()) {
			iter.next();
			iter.remove();
			i++;
		}
		assertEquals(10, i);
		assertFalse(list.iterator().hasNext());
	}

	/**
	 * Runs iterator ten times, each time removing the last element in the list.
	 */
	@Test
	public void removingAllFromEnd() {
		IntDoublyLinkedList list = new IntDoublyLinkedList();
		IntStream.range(0, 10).forEach(list::add);
		for (int i = 10; i != 0; i--) {
			int j = 0;
			Iterator<IntDoublyLinkedList.Node> iter = list.iterator();
			while (iter.hasNext()) {
				iter.next();
				j++;
				if (i == j) {
					iter.remove();
					assertFalse(iter.hasNext());
				}
			}
		}
		assertFalse(list.iterator().hasNext());
	}

}
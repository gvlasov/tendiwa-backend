package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tendiwa.core.meta.Range;

public class RangeTest {

@Test
public void testIntersection() {
	assertEquals(new Range(0, 10).intersection(new Range(5, 190)), new Range(5, 10));
	assertEquals(new Range(0, 10).intersection(new Range(10, 190)), new Range(10, 10));
	assertEquals(new Range(-3, -1).intersection(new Range(-17, -3)), new Range(-3, -3));
	assertEquals(new Range(-3, -1).intersection(new Range(-3, 200)), new Range(-3, -1));
	assertEquals(new Range(-300, 300).intersection(new Range(-600, 600)), new Range(-300, 300));
	assertEquals(new Range(-300, 300).intersection(new Range(-100, 100)), new Range(-100, 100));
	assertEquals(new Range(-17, 17).intersection(new Range(-17, 17)), new Range(-17, 17));
	assertEquals(new Range(-17, -17).intersection(new Range(-17, -17)), new Range(-17, -17));
}

}

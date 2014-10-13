package org.tendiwa.math;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.*;

public class IntersectingSetsFillerTest {
	String a = "Hello";
	String b = "Goodbye";
	String c = "Greetings";
	String d = "Hola";
	String e = "Alloha";
	String f = "Hi";
	String g = "Bye";
	Set<String> superset = new HashSet<String>() {
		{
			add(a);
			add(b);
			add(c);
			add(d);
			add(e);
			add(f);
			add(g);
		}
	};
	Set<Set<String>> subsets = new HashSet<Set<String>>() {
		{
			add(new HashSet<String>() {
				{
					add(a);
					add(b);
					add(c);
					add(f);
				}
			});
			add(new HashSet<String>() {
				{
					add(c);
					add(d);
					add(g);
					add(f);
				}
			});
			add(new HashSet<String>() {
				{
					add(g);
					add(a);
					add(c);
					add(b);
					add(e);
					add(f);
				}
			});
		}
	};

	@Test
	public void test() {
		ImmutableMap<String, Set<String>> answer = new IntersectingSetsFiller<>(
			superset,
			subsets,
			(set) -> set.size() - 1,
			new Random(3)
		).getAnswer();
		Map<Set<String>, Set<String>> map = new HashMap<>();
		for (Map.Entry<String, Set<String>> e : answer.entrySet()) {
			if (!map.containsKey(e.getValue())) {
				map.put(e.getValue(), new HashSet<>());
			}
			map.get(e.getValue()).add(e.getKey());
		}

		System.out.println(map);
	}

	public static void main(String[] args) {
		new IntersectingSetsFillerTest().test();
	}

}
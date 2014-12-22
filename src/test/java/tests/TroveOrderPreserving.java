package tests;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.junit.Test;

import java.util.Arrays;

public class TroveOrderPreserving {
	@Test
	public void mapShouldPreserveKeyOrdering() {
		for (int i=0; i<100; i++) {
			TObjectIntMap<Object> map = createNewMap();
			System.out.println(Arrays.toString(map.keys()));
		}
	}

	private TObjectIntMap<Object> createNewMap() {
		TObjectIntMap<Object> map = new TObjectIntHashMap<>();
		populate(map);
		return map;
	}

	private void populate(TObjectIntMap<Object> map) {
		map.put(new Object(), 1);
		map.put("vagina", 2);
		map.put("sex", 3);
		map.put("garish", 4);
		map.put("gaga", 5);
		map.put("fox", 6);
	}
}

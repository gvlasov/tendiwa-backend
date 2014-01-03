package org.tendiwa.groovy;

import java.util.HashMap;
import java.util.Map;

public class Registry<T> {
private Map<String, T> map = new HashMap<>();

void propertyMissing(String name, T value) {
	map.put(name, value);
}

T propertyMissing(String name) {
	return map.get(name);
}
}

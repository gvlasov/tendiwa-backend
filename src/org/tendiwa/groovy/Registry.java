package org.tendiwa.groovy;

import tendiwa.core.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Registry<T> implements Iterable<T> {
public static Registry<FloorType> floorTypes = new Registry<>();
public static Registry<WallType> wallTypes = new Registry<>();
public static Registry<CharacterAbility> characterAbilities = new Registry<>();
private static Registry<AmmunitionType> ammunitionTypes = new Registry<>();
private static Registry<ItemType> itemTypes = new Registry<>();
private static Registry<Material> materials = new Registry<>();
private static Registry<ObjectType> objectTypes = new Registry<>();
private static Registry<Spell> spells = new Registry<>();
private static Registry<SoundType> soundTypes = new Registry<>();
private static Registry<CharacterType> characters = new Registry<>();
private static Registry<CharacterType> borderObjectTypes = new Registry<>();
private Map<String, T> map = new HashMap<>();
static {
	wallTypes.map.put("void", WallType.VOID);
}

void propertyMissing(String name, T value) {
	map.put(name, value);
}

T propertyMissing(String name) {
	return map.get(name);
}

public T get(String name) {
	return map.get(name);
}

@Override
public Iterator iterator() {
	return map.values().iterator();
}
}

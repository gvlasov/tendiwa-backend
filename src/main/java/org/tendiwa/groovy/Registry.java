package org.tendiwa.groovy;

import org.tendiwa.core.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;

public class Registry<T> implements Iterable<T> {
	public static final Registry<FloorType> floorTypes = new Registry<>();
	public static final Registry<WallType> wallTypes = new Registry<>();
	public static final Registry<CharacterAbility> characterAbilities = new Registry<>();
	private static final Registry<AmmunitionType> ammunitionTypes = new Registry<>();
	private static final Registry<ItemType> itemTypes = new Registry<>();
	private static final Registry<Material> materials = new Registry<>();
	private static final Registry<ObjectType> objectTypes = new Registry<>();
	private static final Registry<Spell> spells = new Registry<>();
	private static final Registry<SoundType> soundTypes = new Registry<>();
	private static final Registry<CharacterType> characters = new Registry<>();
	private static final Registry<BorderObjectType> borderObjectTypes = new Registry<>();

	static {
		wallTypes.map.put("void", WallType.VOID);
		borderObjectTypes.map.put("void", BorderObjectType.VOID);
	}

	private Map<String, T> map = new HashMap<>();

	private Registry() {

	}

	void propertyMissing(String name, T value) {
		map.put(name, value);
	}

	T propertyMissing(String name) {
		if (!map.containsKey(name)) {
			throw new UnsupportedOperationException("No resource '" + name + "' defined");
		}
		return map.get(name);
	}

	public T get(String name) {
		if (!map.containsKey(name)) {
			throw new UnsupportedOperationException("No floor type '" + name + "' defined");
		}
		return map.get(name);
	}

	@Override
	public Iterator<T> iterator() {
		return map.values().iterator();
	}
}

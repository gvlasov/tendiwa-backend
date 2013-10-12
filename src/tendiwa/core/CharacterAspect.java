package tendiwa.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.util.HashMap;

/**
 * Enum-like static data structure that stores flags for {@link CharacterType} creation that determine CharacterType's
 * nature, like `humanoid`, `animal` or `robot`. Each aspect is identified by both its name and id — a human readable
 * string and a generated integer.
 */
public class CharacterAspect implements GsonForStaticDataSerializable {
private static final HashMap<String, CharacterAspect> aspects = new HashMap<>();
final int id = new UniqueObject().id;
private final String name;

static {
	registerAspect("humanoid");
	registerAspect("animal");
}

/**
 * @param name
 * 	Human readable name of aspent.
 */
private CharacterAspect(String name) {
	this.name = name;
}

public static CharacterAspect getByName(String name) {
	CharacterAspect aspect = aspects.get(name);
	if (aspect == null) {
		throw new RuntimeException("Aspect " + name + " has not been registered; did you misspell it?");
	}
	return aspect;
}

/**
 * Adds a new available CharacterAspect aspect to game.
 *
 * @param name
 * 	Name of aspect.
 */
static void registerAspect(String name) {
	aspects.put(name, new CharacterAspect(name));
}

/**
 * Serializes a {@link CharacterAspect} into JSON/
 *
 * @return A single JsonElement — the id of the CharacterAspect.
 */
@Override
public JsonElement serialize(JsonSerializationContext context) {
	return new JsonPrimitive(name);
}
}

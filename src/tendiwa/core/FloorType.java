package tendiwa.core;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public final class FloorType implements GsonForStaticDataSerializable {
	private final String name;
	private final UniqueObject uniqueness;
	FloorType(String name) {
		uniqueness = new UniqueObject();
		this.name = name;
	}
	@Override
	public JsonElement serialize(JsonSerializationContext context) {
		JsonArray jArray = new JsonArray();
		jArray.add(new JsonPrimitive(name));
		return jArray;
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return uniqueness.id;
	}
	
}

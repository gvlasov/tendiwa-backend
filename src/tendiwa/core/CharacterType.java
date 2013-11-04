package tendiwa.core;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class CharacterType implements GsonForStaticDataSerializable {
	private static short nextId = 0;
	private short id;
	private Set<CharacterAspect> aspects;
	private String name;
	private double weight;
	private double height;
	private DirectedGraph<BodyPartTypeInstance, DefaultEdge> bodyGraph;
	public CharacterType(String name, Set<CharacterAspect> aspects, double weight, double height) {
		this.id = nextId++;
		this.name = name;
		this.aspects = aspects;
		this.weight = weight;
		this.height = height;
		this.bodyGraph = bodyGraph;
	}
	public short getId() {
		return id;
	}
	/**
	 * @return the aspects
	 */
	public HashSet<CharacterAspect> getAspects() {
		return new HashSet<>(aspects);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	public String toString() {
		return name;
	}
	@Override
	public JsonElement serialize(JsonSerializationContext context) {
		JsonArray jArray = new JsonArray();
		JsonArray jAspectsArray = new JsonArray();
		for (CharacterAspect aspect : aspects) {
			jAspectsArray.add(context.serialize(aspect));
		}
		jArray.add(new JsonPrimitive(name));
		jArray.add(jAspectsArray);
		jArray.add(new JsonPrimitive(weight));
		jArray.add(new JsonPrimitive(height));
		jArray.add(context.serialize(bodyGraph));
		return jArray;
	}
}

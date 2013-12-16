package tendiwa.core;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collection;
import java.util.Set;

public abstract class CharacterType implements Resourceable {
private static short nextId = 0;
private Set<CharacterAspect> aspects;
private String name;
private double weight;
private double height;
private DirectedGraph<BodyPartTypeInstance, DefaultEdge> bodyGraph;
private int maxHp;

public CharacterType(String name, double weight, double height, CharacterAspect... aspects) {
	this.name = name;
	ImmutableSet.Builder<CharacterAspect> builder = ImmutableSet.builder();
	for (CharacterAspect aspect : aspects) {
		builder.add(aspect);
	}
	this.aspects = builder.build();
	this.weight = weight;
	this.height = height;
}

/**
 * @return the aspects
 */
public Set<CharacterAspect> getAspects() {
	return aspects;
}

public boolean hasAspect(CharacterAspect aspect) {
	return aspects.contains(aspect);
}

@Override
public String getResourceName() {
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
 * @param height
 * 	the height to set
 */
public void setHeight(double height) {
	this.height = height;
}

public String toString() {
	return name;
}

public abstract Collection<CharacterAbility> getAvailableActions();

public abstract  int getMaxHp();
}

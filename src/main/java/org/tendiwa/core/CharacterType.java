package org.tendiwa.core;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.tendiwa.lexeme.Localizable;

import java.util.Collection;
import java.util.Set;

public class CharacterType implements Resourceable, Localizable {
	public Set<CharacterAspect> aspects;
	public String name;
	private double weight;
	private double height;
	private DirectedGraph<BodyPartTypeInstance, DefaultEdge> bodyGraph;
	private int maxHp;
	private Collection<CharacterAbility> abilities = ImmutableSet.of();

	public CharacterType() {
	}

	public void name(String name) {
		this.name = name;
	}

	public void weight(double weight) {
		this.weight = weight;
	}

	public void height(double height) {
		this.height = height;
	}

	public void aspects(CharacterAspect... aspects) {
		this.aspects = ImmutableSet.copyOf(aspects);
	}

	public void actions(CharacterAbility... actions) {
		this.abilities = ImmutableSet.copyOf(actions);
	}

	public void maxHp(int maxHp) {
		this.maxHp = maxHp;
	}

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

	public Collection<CharacterAbility> getAvailableActions() {
		return abilities;
	}

	public int getMaxHp() {
		return maxHp;
	}

	@Override
	public String getLocalizationId() {
		return getResourceName();
	}
}

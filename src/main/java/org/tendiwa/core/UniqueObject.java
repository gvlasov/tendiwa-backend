package org.tendiwa.core;

/**
 * Manages ids of objects and makes sure that none of two objects inheriting
 * from UniqueObject have the same id.
 */
public final class UniqueObject {
	private static int lastId = 0;
	public final int id;

	public UniqueObject() {
		id = ++lastId;
	}

	public final int getId() {
		return id;
	}

	public final int hashCode() {
		return id;
	}
}

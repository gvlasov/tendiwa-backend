package org.tendiwa.settlements.streets;

@FunctionalInterface
// TODO: Move to liblexeme
public interface Namer<T> {
	/**
	 * @param thing
	 * 	A thing to assign a name to.
	 * @return Localization id of a thing.
	 * @see org.tendiwa.lexeme.Localizable#getLocalizationId()
	 */
	public String name(T thing);
}

package org.tendiwa.settlements.streets;

import org.tendiwa.geometry.Chain2D;

public final class Street {

	private final Chain2D chain;
	private final String localizationId;

	public Street(Chain2D chain, String localizationId) {
		this.chain = chain;
		this.localizationId = localizationId;
	}

	public Chain2D chain() {
		return chain;
	}

}

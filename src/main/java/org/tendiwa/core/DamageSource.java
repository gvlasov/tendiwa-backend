package org.tendiwa.core;

import org.tendiwa.lexeme.Localizable;

public interface DamageSource extends Localizable {

	public DamageSourceType getSourceType();

	enum DamageSourceType {
		CHARACTER, ITEM
	}
}

package org.tendiwa.core;

public class SpellProjectile implements Projectile {
	private final String resourceName;

	public SpellProjectile(String resourceName) {
		this.resourceName = resourceName;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}
}

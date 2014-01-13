package org.tendiwa.core;

public class RequestShoot implements Request {
private final UniqueItem rangedWeapon;
private final Item projectile;
private final int toX;
private final int toY;

public RequestShoot(UniqueItem rangedWeapon, Item projectile, int toX, int toY) {
	this.toX = toX;
	this.toY = toY;
	if (!Items.isShootable(projectile.getType())) {
		throw new RuntimeException("Projectile must be an item with shootable type");
	}
	this.rangedWeapon = rangedWeapon;
	this.projectile = projectile;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().shoot(rangedWeapon, projectile, toX, toY);
}
}

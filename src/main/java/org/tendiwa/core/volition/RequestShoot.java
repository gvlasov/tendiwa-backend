package org.tendiwa.core.volition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;

public class RequestShoot implements Request {
	private final Character player;
	private final UniqueItem rangedWeapon;
	private final Item projectile;
	private final int toX;
	private final int toY;

	@Inject
	public RequestShoot(
		@Named("player") Character player,
		@Assisted UniqueItem rangedWeapon,
		@Assisted Item projectile,
		@Assisted("x") int toX,
		@Assisted("y") int toY
	) {
		this.player = player;
		this.rangedWeapon = rangedWeapon;
		this.projectile = projectile;
		this.toX = toX;
		this.toY = toY;
	}

	@Override
	public void process() {
		if (!Items.isShootable(projectile.getType())) {
			throw new RuntimeException("Projectile must be an item with shootable type");
		}
		player.shoot(rangedWeapon, projectile, toX, toY);

	}

	public interface Factory {
		public RequestShoot create(UniqueItem rangedWeapon, Item projectile, @Assisted("x") int toX, @Assisted("y") int toY);
	}
}

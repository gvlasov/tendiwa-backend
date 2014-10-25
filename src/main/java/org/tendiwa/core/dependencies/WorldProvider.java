package org.tendiwa.core.dependencies;

import com.google.inject.Provider;
import org.tendiwa.core.World;

public class WorldProvider implements Provider<World> {
	private World world;

	public WorldProvider() {

	}

	@Override
	public World get() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}

package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;

@Singleton
public class PlayerWorldProvider implements Provider<World> {
private final PlayerCharacterProvider playerCharacterProvider;

@Inject
PlayerWorldProvider(PlayerCharacterProvider playerCharacterProvider) {

	this.playerCharacterProvider = playerCharacterProvider;
}

@Override
public World get() {
	return playerCharacterProvider.get().getWorld();
}
}

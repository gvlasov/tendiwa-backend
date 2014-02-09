package org.tendiwa.core.player;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.vision.Seer;

@Singleton
public class PlayerSeerProvider implements Provider<Seer> {
private final PlayerCharacterProvider playerCharacterProvider;

@Inject
PlayerSeerProvider(
	PlayerCharacterProvider playerCharacterProvider
) {

	this.playerCharacterProvider = playerCharacterProvider;
}

@Override
public Seer get() {
	return playerCharacterProvider.get().getSeer();
}
}

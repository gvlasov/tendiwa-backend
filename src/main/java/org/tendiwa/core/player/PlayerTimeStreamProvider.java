package org.tendiwa.core.player;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.tendiwa.core.TimeStream;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;

@Singleton
public class PlayerTimeStreamProvider implements Provider<TimeStream> {
	private final PlayerCharacterProvider playerCharacterProvider;

	@Inject
	PlayerTimeStreamProvider(
		PlayerCharacterProvider playerCharacterProvider
	) {

		this.playerCharacterProvider = playerCharacterProvider;
	}

	@Override
	public TimeStream get() {
		return playerCharacterProvider.get().getTimeStream();
	}
}

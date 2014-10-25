package org.tendiwa.core.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.tendiwa.core.Character;
import org.tendiwa.core.World;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;

@Singleton
public class SinglePlayerMode {
	private final PlayerCharacterProvider playerCharacterProvider;

	@Inject
	SinglePlayerMode(
		PlayerCharacterProvider playerCharacterProvider
	) {
		this.playerCharacterProvider = playerCharacterProvider;
	}

	public void setPlayerCharacter(Character playerCharacter, World world) {
//	playerCharacter.setPlane(world.getDefaultPlane());
//	playerCharacter.setWorld(world);
		playerCharacterProvider.setCharacter(playerCharacter);
//	model.emitEvent(new EventSelectPlayerCharacter(playerCharacter, world));
	}

	public boolean isPlayer(Character character) {
		return character == playerCharacterProvider.get();
	}
}

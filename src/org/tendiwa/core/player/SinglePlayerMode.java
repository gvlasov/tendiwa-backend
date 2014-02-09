package org.tendiwa.core.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.Character;
import org.tendiwa.core.TimeStream;
import org.tendiwa.core.World;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.events.EventSelectPlayerCharacter;
import org.tendiwa.core.observation.Observable;

@Singleton
public class SinglePlayerMode {
private final PlayerCharacterProvider playerCharacterProvider;
private final Observable model;
private final TimeStream timeStream;

@Inject
SinglePlayerMode(
	PlayerCharacterProvider playerCharacterProvider,
	@Named("tendiwa") Observable model,
	@Named("player") TimeStream timeStream
) {
	this.playerCharacterProvider = playerCharacterProvider;
	this.model = model;
	this.timeStream = timeStream;
}

public void setPlayerCharacter(Character playerCharacter, World world) {
	playerCharacter.setTimeStream(timeStream);
	playerCharacter.setPlane(world.getDefaultPlane());
	playerCharacter.setWorld(world);
	timeStream.addPlayerCharacter(playerCharacter);
	playerCharacterProvider.setCharacter(playerCharacter);
	model.emitEvent(new EventSelectPlayerCharacter(playerCharacter, world));
}
}

package org.tendiwa.core.player;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.vision.Seer;

public class PlayerModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Character.class)
			.annotatedWith(Names.named("player"))
			.toProvider(PlayerCharacterProvider.class);
		bind(World.class)
			.annotatedWith(Names.named("current_player_world"))
			.toProvider(PlayerWorldProvider.class);
		bind(TimeStream.class)
			.annotatedWith(Names.named("player"))
			.toProvider(PlayerTimeStreamProvider.class)
			.in(Scopes.SINGLETON);
		bind(Seer.class)
			.annotatedWith(Names.named("player_seer"))
			.toProvider(PlayerSeerProvider.class)
			.in(Scopes.SINGLETON);
	}
}

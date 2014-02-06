package org.tendiwa.core;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.factories.CharacterFactory;
import org.tendiwa.core.factories.TimeStreamFactory;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.vision.Seer;

public class TendiwaBackendModule extends AbstractModule {
@Override
protected void configure() {
	bind(Tendiwa.class).in(Scopes.SINGLETON);
	bind(PlayerCharacterProvider.class)
		.in(Scopes.SINGLETON);
	bind(Character.class)
		.annotatedWith(Names.named("player"))
		.toProvider(PlayerCharacterProvider.class);
	bind(Observable.class)
		.annotatedWith(Names.named("tendiwa"))
		.to(Tendiwa.class);
	bind(World.class)
		.annotatedWith(Names.named("current_player_world"))
		.toProvider(PlayerWorldProvider.class);
	bind(Seer.class)
		.annotatedWith(Names.named("player_seer"))
		.toProvider(PlayerSeerProvider.class);
	bind(TimeStream.class)
		.annotatedWith(Names.named("player_time_stream"))
		.to(TimeStream.class)
		.in(Scopes.SINGLETON);
	install(new FactoryModuleBuilder()
		.implement(Character.class, Character.class)
		.build(CharacterFactory.class));
	install(new FactoryModuleBuilder()
		.build(TimeStreamFactory.class));
}
}

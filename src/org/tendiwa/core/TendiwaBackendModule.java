package org.tendiwa.core;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;

public class TendiwaBackendModule extends AbstractModule {
@Override
protected void configure() {
	bind(Tendiwa.class).in(Scopes.SINGLETON);
	bind(PlayerCharacterProvider.class)
		.in(Scopes.SINGLETON);
	bind(Character.class)
		.annotatedWith(Names.named("player"))
		.toProvider(PlayerCharacterProvider.class);
}
}

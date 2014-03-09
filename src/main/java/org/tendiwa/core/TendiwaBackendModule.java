package org.tendiwa.core;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import org.tendiwa.core.dependencies.PlayerCharacterProvider;
import org.tendiwa.core.factories.CharacterFactory;
import org.tendiwa.core.factories.NpcFactory;
import org.tendiwa.core.factories.TimeStreamFactory;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.ThreadProxy;

public class TendiwaBackendModule extends AbstractModule {
@Override
protected void configure() {
	bind(Tendiwa.class).in(Scopes.SINGLETON);
	bind(PlayerCharacterProvider.class)
		.in(Scopes.SINGLETON);
	bind(Observable.class)
		.annotatedWith(Names.named("tendiwa"))
		.to(Tendiwa.class);
	bind(Server.class)
		.in(Scopes.SINGLETON);
	bind(ThreadProxy.class)
		.toProvider(ThreadProxyProvider.class)
		.in(Scopes.SINGLETON);
	install(new FactoryModuleBuilder()
		.build(CharacterFactory.class));
	install(new FactoryModuleBuilder()
		.build(NpcFactory.class));
	install(new FactoryModuleBuilder()
		.build(TimeStreamFactory.class));
}
}

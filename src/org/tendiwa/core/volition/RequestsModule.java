package org.tendiwa.core.volition;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RequestsModule extends AbstractModule {
@Override
protected void configure() {
	install(new FactoryModuleBuilder()
		.build(RequestShoot.Factory.class));
	install(new FactoryModuleBuilder()
		.build(RequestMove.Factory.class));
	install(new FactoryModuleBuilder()
		.build(RequestPickUp.Factory.class));
	install(new FactoryModuleBuilder()
		.build(RequestPropel.Factory.class));
	install(new FactoryModuleBuilder()
		.build(RequestActionToCell.Factory.class));
	install(new FactoryModuleBuilder()
		.build(RequestActionWithoutTarget.Factory.class));
}
}

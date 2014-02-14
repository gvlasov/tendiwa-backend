package org.tendiwa.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.observation.ThreadProxy;

@Singleton
public class ThreadProxyProvider implements Provider<ThreadProxy> {
private final Observable model;

@Inject
ThreadProxyProvider(
	@Named("tendiwa") Observable model
) {

	this.model = model;
}

@Override
public ThreadProxy get() {
	return new ThreadProxy(model);
}
}

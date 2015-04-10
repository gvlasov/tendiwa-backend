package org.tendiwa.core;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.tendiwa.core.events.*;
import org.tendiwa.core.observation.Observable;
import org.tendiwa.core.player.PlayerModule;
import org.tendiwa.core.volition.RequestsModule;

import java.util.LinkedList;
import java.util.List;

import static org.tendiwa.geometry.DSL.rectangle;

@Singleton
public class Tendiwa extends Observable {
	public static final String MODULES_CONF_FILE = "/modules.conf";
	private static List<Class<? extends Module>> modulesCreatingWorlds;
	private static Injector injector;
	private final TimeStreamManager timeStreamManager;
	private final Server server;

	@Inject
	public Tendiwa(
		TimeStreamManager timeStreamManager,
		Server server

	) {
		this.timeStreamManager = timeStreamManager;
		this.server = server;
		initEmitters();
	}

	public static Injector getInjector() {
		if (injector == null) {
			injector = Guice.createInjector(new TendiwaBackendModule(), new PlayerModule(), new RequestsModule());
		}
		return injector;
	}

	public static Tendiwa newBackend() {
		return getInjector().getInstance(Tendiwa.class);
	}

	public static void main(String args[]) {
		if (args.length > 0 && args[0].equals("--ontology")) {
			// Ontology building utility
			String moduleDir = args[1];
			if (moduleDir == null) {
				throw new RuntimeException("Modules directory not provided");
			}
		}
	}

	/**
	 * Builds ontologies from all modules in a single Groovy shell. The modules used are the modules listed int
	 * {@link #MODULES_CONF_FILE}.
	 *
	 * @return
	 */
	public static List<Class<? extends Module>> loadModules() {
		modulesCreatingWorlds = new LinkedList<>();
//		for (String module : modules) {
//		Class<?> moduleClass = classLoader.loadClass("tendiwa.modules." + module);
//		if (!Module.class.isAssignableFrom(moduleClass)) {
//			logger.warn(moduleClass + " is not a module class");
//			continue;
//		}
//		if (WorldProvidingModule.class.isAssignableFrom(moduleClass)) {
//			modulesCreatingWorlds.add(moduleClass);
//		}
//			modulesCreatingWorlds.add(new ScriptShell(module).getModuleClass());
//		}
		new ScriptShell(MODULES_CONF_FILE);
//		if (modulesCreatingWorlds.size() == 0) {
//			throw new RuntimeException("No world-creating modules provided");
//		}
//		throw new UnsupportedOperationException();
//		return modulesCreatingWorlds;
		return null;
	}

	public static Module getMainModule() {
		try {
			return modulesCreatingWorlds.iterator().next().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void initEmitters() {
		createEventEmitter(EventGetDamage.class);
		createEventEmitter(EventGetItem.class);
		createEventEmitter(EventLoseItem.class);
		createEventEmitter(EventFovChange.class);
//	createEventEmitter(EventSelectPlayerCharacter.class);
		createEventEmitter(EventInitialTerrain.class);
		createEventEmitter(EventMoveToPlane.class);
		createEventEmitter(EventPutOn.class);
		createEventEmitter(EventTakeOff.class);
		createEventEmitter(EventWield.class);
		createEventEmitter(EventUnwield.class);
		createEventEmitter(EventMove.class);
		createEventEmitter(EventItemAppear.class);
		createEventEmitter(EventItemDisappear.class);
		createEventEmitter(EventSound.class);
		createEventEmitter(EventDie.class);
		createEventEmitter(EventAttack.class);
		createEventEmitter(EventProjectileFly.class);
		createEventEmitter(EventExplosion.class);
	}

	public void start() {
		World world = new World(rectangle(100, 100));
		timeStreamManager.populate(world);

		// Starting server
		server.start();
	}
}

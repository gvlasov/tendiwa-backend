package org.tendiwa.core;

import com.sun.nio.sctp.AssociationChangeNotification;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Tendiwa {
private static final Object lock = new Object();
private static final Object clientWaitLock = new Object();
private static final String MODULES_CONF_FILE = "/modules.conf";
public static TendiwaClient CLIENT;
private static Tendiwa INSTANCE;
private static AssociationChangeNotification clientEventManager;
private static int worldWidth;
private static int worldHeight;
private static boolean eventComputed = false;
public final org.apache.log4j.Logger logger = Logger.getLogger("org/tendiwa");
public final Server SERVER = Server.SERVER;
private final Thread SERVER_THREAD;
private final String CLIENT_CONF_FILE;
private final World WORLD;
private final Character PLAYER;
private static List<Class<?>> modulesCreatingWorlds;

Tendiwa(String args[]) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
	initWithDummyClient();
	// Run game server and client.
	ClassLoader classLoader = Tendiwa.class.getClassLoader();

	// Loading modules
	List<Class<?>> modulesCreatingWorlds = loadModules();
	createWorld((WorldProvider) modulesCreatingWorlds.get(0).newInstance());

	// Initializing client
	CLIENT_CONF_FILE = "/client.conf";
	InputStream clientConfStream = Tendiwa.class.getResourceAsStream(CLIENT_CONF_FILE);
	Properties properties = new Properties();
	if (clientConfStream == null) {
		// Use default properties
		properties.setProperty("client", "org.tendiwa.client.TendiwaGame");
	} else {
		properties.load(clientConfStream);
	}

	// Starting server
	SERVER_THREAD = new Thread(Server.SERVER);
	SERVER_THREAD.start();

	// Starting client
	CLIENT = (TendiwaClient) classLoader.loadClass(properties.getProperty("client")).newInstance();
	CLIENT.startup();

	WORLD = Server.SERVER.getWorld();
	worldWidth = WORLD.getWidth();
	worldHeight = WORLD.getHeight();
	PLAYER = WORLD.getPlayer();
}

public static List<Class<?>> loadModules() {
	String[] modules = Module.getModulesFromConfig(MODULES_CONF_FILE);
	modulesCreatingWorlds = new LinkedList<>();
	for (String module : modules) {
//		Class<?> moduleClass = classLoader.loadClass("tendiwa.modules." + module);
//		if (!Module.class.isAssignableFrom(moduleClass)) {
//			logger.warn(moduleClass + " is not a module class");
//			continue;
//		}
//		if (WorldProvider.class.isAssignableFrom(moduleClass)) {
//			modulesCreatingWorlds.add(moduleClass);
//		}
		modulesCreatingWorlds.add(new ScriptShell(module).getModuleClass());
	}
	if (modulesCreatingWorlds.size() == 0) {
		throw new RuntimeException("No world-creating modules provided");
	}
	return modulesCreatingWorlds;
}
public static Module getMainModule() {
	try {
		return (Module) modulesCreatingWorlds.iterator().next().newInstance();
	} catch (InstantiationException | IllegalAccessException e) {
		throw new RuntimeException(e);
	}
}

public static void createWorld(WorldProvider worldProvider) {
	Server.SERVER.setWorld(worldProvider);
	if (Server.SERVER.getWorld().getPlayer() == null) {
		throw new RuntimeException("WorldProvider module did not specify the initial position of player character");
	}
}

public static void initWithDummyClient() {
	CLIENT = new DummyClient();
}

public static void main(String args[]) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
	if (args.length > 0 && args[0].equals("--ontology")) {
		// Ontology building utility
		String moduleDir = args[1];
		if (moduleDir == null) {
			throw new RuntimeException("Modules directory not provided");
		}
	} else {
		INSTANCE = new Tendiwa(args);
	}
}

public static World getWorld() {
	return Server.SERVER.getWorld();
}

public static Thread getServerThread() {
	return INSTANCE.SERVER_THREAD;
}

public static TendiwaClient getClient() {
	return CLIENT;
}

public static Server getServer() {
	return Server.SERVER;
}

public static TendiwaClientEventManager getClientEventManager() {
	return CLIENT.getEventManager();
}

public static Logger getLogger() {
	return INSTANCE.logger;
}

/**
 * <p>Returns width of world in cells. It is equivalent to {@code Tendiwa.getWorld().getWidth()}, just more convenient
 * and fast, as world width and height are often needed by different parts of game.</p> <p>Use this method strictly
 * after the world has been initialized.</p>
 *
 * @return Width of world in cells.
 * @see Tendiwa#getWorldHeight()
 */
public static int getWorldWidth() {
	return worldWidth;
}

/**
 * <p>Returns height of world in cells. It is equivalent to {@code Tendiwa.getWorld().getHeight()}, just more convenient
 * and fast, as world width and height are often needed by different parts of game.</p> <p>Use this method strictly
 * after the world has been initialized.</p>
 *
 * @return Height of world in cells.
 * @see Tendiwa#getWorldWidth() ()
 */
public static int getWorldHeight() {
	return worldHeight;
}

public static Character getPlayerCharacter() {
	return INSTANCE.PLAYER;
}

public static void waitForAnimationToStartAndComplete() {
	eventComputed = true;
	synchronized (lock) {
		lock.notify();
		while (!getClient().isAnimationCompleted()) {
			try {
				lock.wait();
			} catch (InterruptedException ignored) {
			}
		}
		eventComputed = false;
	}
}

public static void signalAnimationCompleted() {
	synchronized (lock) {
		lock.notify();
	}
}

public static Object getLock() {
	return lock;
}

public static boolean isEventComputed() {
	return eventComputed;
}
}

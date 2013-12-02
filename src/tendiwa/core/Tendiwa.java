package tendiwa.core;

import com.sun.nio.sctp.AssociationChangeNotification;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
//import tendiwa.modules.SuseikaBrowserClientResourceBuilder;

public class Tendiwa {
private static Tendiwa INSTANCE;
private static AssociationChangeNotification clientEventManager;
private static int worldWidth;
private static int worldHeight;
public final org.apache.log4j.Logger logger = Logger.getLogger("tendiwa");
public final Server SERVER = Server.SERVER;
public final TendiwaClient CLIENT;
private final Thread SERVER_THREAD;
private final String MODULES_CONF_FILE = "/modules.conf";
private final String CLIENT_CONF_FILE;
private final World WORLD;
private final PlayerCharacter PLAYER;

Tendiwa(String args[]) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
	// Run game server and client.
	ClassLoader classLoader = Tendiwa.class.getClassLoader();

	// Loading modules
	String[] modules = Module.getModulesFromConfig(MODULES_CONF_FILE);
	List<Class<?>> modulesCreatingWorlds = new LinkedList<>();
	for (String module : modules) {
		Class<?> moduleClass = classLoader.loadClass("tendiwa.modules." + module);
		if (!Module.class.isAssignableFrom(moduleClass)) {
			logger.warn(moduleClass + " is not a module class");
			continue;
		}
		if (WorldProvider.class.isAssignableFrom(moduleClass)) {
			modulesCreatingWorlds.add(moduleClass);
		}
	}
	if (modulesCreatingWorlds.size() == 0) {
		throw new RuntimeException("No world-creating modules provided");
	}

	// Creating world
	WorldProvider worldProvider = (WorldProvider) modulesCreatingWorlds.get(0).newInstance();
	Server.SERVER.setWorld(worldProvider);
	if (SERVER.getWorld().getPlayerCharacter() == null) {
		throw new RuntimeException("WorldProvider module did not specify the initial position of PlayerCharacter");
	}

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
	PLAYER = WORLD.getPlayerCharacter();
}

public static void main(String args[]) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
	if (args.length > 0 && args[0].equals("--ontology")) {
		// Ontology building utility
		String moduleDir = args[1];
		if (moduleDir == null) {
			throw new RuntimeException("Modules directory not provided");
		}
		ModuleBuilder.generateResourcesCode(moduleDir);
	} else {
		INSTANCE = new Tendiwa(args);
	}
}

public static World getWorld() {
	return INSTANCE.WORLD;
}

public static PlayerCharacter getPlayer() {
	return INSTANCE.PLAYER;
}

public static Thread getServerThread() {
	return INSTANCE.SERVER_THREAD;
}

public static TendiwaClient getClient() {
	return INSTANCE.CLIENT;
}

public static Server getServer() {
	return Server.SERVER;
}

public static TendiwaClientEventManager getClientEventManager() {
	return INSTANCE.CLIENT.getEventManager();
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
 * @see tendiwa.core.Tendiwa#getWorldHeight()
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
}

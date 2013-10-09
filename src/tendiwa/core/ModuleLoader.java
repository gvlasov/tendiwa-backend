package tendiwa.core;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Manages loading of various game {@link Module}s
 */
public class ModuleLoader {
private static Map<Class<? extends Module>, Module> modules = new HashMap<Class<? extends Module>, Module>();
private static File modulesDir;
private static ClassLoader classLoader;
private static List<Class<? extends Location>> drawers = new ArrayList<>();
private static List<Class<? extends LocationFeature>> features = new ArrayList<>();

public ModuleLoader() {}

/**
 * Loads modules so the engine can use their data and classes. This method also
 * checks if the modules' dependencies are satisfied.
 */
public static void loadModules() {
	File[] files = modulesDir.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File file, String name) {
			if (name.endsWith(".jar")) {
				return true;
			}
			return false;
		}
	});
	if (files == null) {
		throw new Error("No modules loaded from " + modulesDir + " directory");
	}
	URL[] urls = new URL[files.length];
	try {
		int i = 0;
		for (File file : files) {
			urls[i++] = file.toURL();
			String name = file.getName();
		}
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	classLoader = new URLClassLoader(urls, ModuleLoader.class.getClassLoader());
	for (File jar : files) {
		String moduleName = jar.getName().replace(".jar", "");
		try {
			Class<? extends Module> moduleClass = (Class<? extends Module>) classLoader.loadClass("tendiwa.modules." + moduleName);

			modules.put(moduleClass, (Module) moduleClass.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// Check for dependencies and run modules
	for (Map.Entry<Class<? extends Module>, Module> entry : modules.entrySet()) {
		entry.getValue().checkForDependencies();
		entry.getValue().buildStaticData(entry.getKey());
	}
}
public static void setModulesDirectory(String path) {
	File dir = new File(path);
	if (!dir.isDirectory()) {
		throw new Error(path + " is not a directory");
	}
	modulesDir = dir;
}

public static boolean hasModuleLoaded(String dependency) {
	for (Class<? extends Module> cls : modules.keySet()) {
		if (cls.getName() == dependency) {
			return true;
		}
	}
	return false;
}

}
package tendiwa.core;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Manages loading of various game {@link Module}s
 */
public class ModuleLoader {
static HashMap<String, Module> modules = new HashMap<String, Module>();
static Collection<String> moduleNames = new HashSet<String>();
private static final File modulesDir = new File("/home/suseika/Projects/workspace/tendiwa_server/modules/");
private static final ModuleLoader instance = new ModuleLoader();
public String test = "hey hey hye";
private final static ClassLoader classLoader;
static {
	File[] files = modulesDir.listFiles(new FilenameFilter() {
		@Override
		public boolean accept(File file, String name) {
			if (name.endsWith(".jar")) {
				return true;
			}
			return false;
		}
	});
	URL[] urls = new URL[files.length];
	try {
		int i = 0;
		for (File file : files) {
			urls[i++] = file.toURL();
			String name = file.getName();
			moduleNames.add(name.substring(0, name.length()-4));
		}
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	classLoader = new URLClassLoader(urls, ModuleLoader.class.getClassLoader());
}

/**
 * Loads modules so the engine can use their data and classes. This method also
 * checks if the modules' dependencies are satisfied.
 */
public static void loadModules() {
		for (String moduleName : moduleNames) {
			try {
				classLoader.loadClass("tendiwa.modules.MainModule");
				Class moduleClass = classLoader.loadClass("tendiwa.modules."+moduleName);

				modules.put(moduleName, (Module)moduleClass.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Check for dependencies and run modules
		for (Module module : modules.values()) {
			module.checkForDependencies();
			module.buildStaticData();
		}
	}
}
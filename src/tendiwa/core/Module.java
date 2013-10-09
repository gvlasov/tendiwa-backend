package tendiwa.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class Module {
private Collection<String> dependencies = new HashSet<>();
private Collection<String> resourcePaths = new HashSet<>();

protected Module() {
}

public void buildStaticData(Class<? extends Module> subclass) {

	for (String pathToResource : resourcePaths) {
		System.out.println(subclass.getResource(pathToResource));
		LoadStaticDataFromXML.loadGameDataFromXml(subclass, pathToResource);
	}
}

public Collection<String> getDependencies() {
	return dependencies;
}

/**
 * Sets an enumeration of names of other modules that are required to be added to a distribution for this module to
 * work.
 *
 * @param dependency
 * 	Names of modules
 */
public void addDependency(String dependency) {
	dependencies.add(dependency);
}

/**
 * Checks if all the modules that are required for this module to work are loaded by {@link
 * tendiwa.core.ModuleLoader}.
 */
void checkForDependencies() throws DependencyNotSatisfiedException {
	HashSet<String> unsatisfiedDependencies = new HashSet<String>();
	for (String dependency : dependencies) {
		if (!ModuleLoader.hasModuleLoaded(dependency)) {
			unsatisfiedDependencies.add(dependency);
		}
	}
	if (unsatisfiedDependencies.size() > 0) {
		throw new DependencyNotSatisfiedException(unsatisfiedDependencies);
	}
}

/**
 * Registers a file within module's Jar file to be exported into {@link StaticData} on module loading.
 *
 * @param path
 * 	Path to a file within a jar.
 */
protected void addStaticDataResource(String path) {
	resourcePaths.add(path);
}

}

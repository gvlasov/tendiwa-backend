package tendiwa.core;

import java.util.Collection;
import java.util.HashSet;

public abstract class Module {
private Collection<String> dependencies = new HashSet<>();
private Collection<String> resourcePaths = new HashSet<>();

protected Module() {
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
 * Registers a file within module's Jar file to be exported into {@link StaticData} on module loading.
 *
 * @param path
 * 	Path to a file within a jar.
 */
protected void addStaticDataResource(String path) {
	resourcePaths.add(path);
}

}

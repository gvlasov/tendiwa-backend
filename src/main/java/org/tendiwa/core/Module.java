package org.tendiwa.core;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;

public abstract class Module {
	private Collection<String> dependencies = new HashSet<>();
	private Collection<String> resourcePaths = new HashSet<>();

	protected Module() {
	}

	/**
	 * Loads names of Module implementations from a resource.
	 *
	 * @param resource
	 * 	Path to resource.
	 * @return Names of all modules's classes.
	 */
	public static String[] getModulesFromConfig(String resource) {
		InputStream resourceAsStream;
		try {
			resourceAsStream = Tendiwa.class.getResourceAsStream(resource);
			if (resourceAsStream == null) {
				throw new RuntimeException("No " + resource + " resource provided");
			}
			String[] split = CharStreams
				.toString(new InputStreamReader(resourceAsStream, Charsets.UTF_8))
				.split(System.getProperty("line.separator"));
			resourceAsStream.close();
			return split;
		} catch (IOException e) {
			throw new RuntimeException("Could not read modules names from resource " + resource);
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
	 * Registers a file within module's Jar file to be exported into {@link StaticData} on module loading.
	 *
	 * @param path
	 * 	Path to a file within a jar.
	 */
	protected void addStaticDataResource(String path) {
		resourcePaths.add(path);
	}

}

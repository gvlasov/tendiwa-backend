package org.tendiwa.core;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.reflection.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.regex.Pattern;

public class ScriptShell extends GroovyShell {
	public ScriptShell(String modulesConfFilepath) {
		super(ScriptShell.class.getClassLoader());
		List<GroovyCodeSource> sources = collectGameObjectDeclarationFiles(modulesConfFilepath);
		System.out.println(sources.size());
		sources.forEach(this::evaluate);
	}

	private static List<GroovyCodeSource> collectGameObjectDeclarationFiles(String modulesConfFilepath) {
		String[] modules = Module.getModulesFromConfig(modulesConfFilepath);
		List<GroovyCodeSource> answer = new LinkedList<>();
		for (String moduleName : modules) {
			GroovyCodeSource codeSource;
			Reflections reflections = new Reflections(
				new ConfigurationBuilder()
					.addUrls(ClasspathHelper.forPackage(moduleName + ".ontology"))
					.addScanners(new ResourcesScanner())
					.filterInputsBy(new FilterBuilder().includePackage(moduleName))
			);
			Set<String> resourcePaths = reflections.getResources(Pattern.compile(".*\\.groovy"));
			for (String resourcePath : resourcePaths) {
				URL resource = reflections.getClass().getResource("/" + resourcePath);
				if (resource == null) {
					throw new MissingResourceException(
						"No file with character types definitions",
						null,
						null
					);
				}
				try {
					codeSource = new GroovyCodeSource(resource);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				System.out.println(codeSource.getName());
				answer.add(codeSource);
			}
		}
		return answer;
	}

	public static void loadGameTypesDeclarations(Script script) {
		for (GroovyCodeSource source : collectGameObjectDeclarationFiles(Tendiwa.MODULES_CONF_FILE)) {
			script.evaluate(source.getScriptText());
		}
	}
}

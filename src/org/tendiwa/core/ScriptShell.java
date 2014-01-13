package org.tendiwa.core;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;

import java.io.IOException;

public class ScriptShell {

private final Class<? extends Module> moduleClass;

public ScriptShell(String moduleName) {
	Binding binding = new Binding();
	GroovyShell shell = new GroovyShell(binding);
	GroovyCodeSource codeSource;
	try {
		codeSource = new GroovyCodeSource(getClass().getResource("/Characters.groovy"));
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
	shell.evaluate(codeSource);
	GroovyCodeSource module = null;
	try {
		module = new GroovyCodeSource(getClass().getResource("/" + moduleName + ".groovy"));
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
	GroovyClassLoader gcl = new GroovyClassLoader(getClass().getClassLoader());
	moduleClass = (Class<? extends Module>) gcl.parseClass(module);

}

public Class<? extends Module> getModuleClass() {
	return moduleClass;
}
}

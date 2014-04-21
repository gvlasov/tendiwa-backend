package org.tendiwa.core;

import groovy.lang.*;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;

public class ScriptShell {

    private final Class<? extends Module> moduleClass;

    public ScriptShell(String moduleName) {
        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(this.getClass().getClassLoader());
        GroovyCodeSource codeSource;
        URL resource = getClass().getResource("/Characters.groovy");
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

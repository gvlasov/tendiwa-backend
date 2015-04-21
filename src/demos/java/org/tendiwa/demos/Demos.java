package org.tendiwa.demos;

import com.google.inject.Guice;
import com.google.inject.Module;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.tendiwa.core.ScriptShell;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.World;
import org.tendiwa.core.worlds.Genesis;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingWorld;

public final class Demos {
	private Demos() {
		throw new UnsupportedOperationException();
	}

	public static TestCanvas createCanvas() {
		return Guice.createInjector(new DrawingModule()).getInstance(TestCanvas.class);
	}

	/**
	 * A quick trick to put current thread to sleep until the whole application is killed.
	 */
	public static void sleepIndefinitely() {
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void run(Class<? extends Runnable> demoClass) {
		Guice.createInjector(new DrawingModule()).getInstance(demoClass).run();
	}

	public static void run(Class<? extends Runnable> demoClass, Module... modules) {
		Guice.createInjector(modules).getInstance(demoClass).run();
	}

	public static void genesis(Class<? extends Genesis> genesisClass, Module... modules) {
		Tendiwa.loadModules();
		Guice.createInjector(modules).getInstance(genesisClass).world();
	}
}

package org.tendiwa.demos;

import com.google.inject.Guice;
import com.google.inject.Module;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.tendiwa.core.ScriptShell;
import org.tendiwa.core.Tendiwa;
import org.tendiwa.core.World;
import org.tendiwa.core.WorldProvidingModule;
import org.tendiwa.core.dependencies.WorldProvider;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
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

	public static void drawWorld(Class<? extends WorldProvidingModule> worldProvidingModuleClass) {
		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		GroovyShell shell = new ScriptShell(Tendiwa.MODULES_CONF_FILE);
		Tendiwa.loadModules();
		org.tendiwa.core.Module mainModule = Tendiwa.getMainModule();
		World world = Tendiwa.createWorld(worldProvidingModuleClass);
		TestCanvas canvas = new TestCanvas(1, world.getWidth(), world.getHeight());
		canvas.draw(world, DrawingWorld.defaultAlgorithm());
	}
}

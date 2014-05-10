package org.tendiwa.demos;

import com.google.inject.Guice;
import com.google.inject.Module;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;

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
}

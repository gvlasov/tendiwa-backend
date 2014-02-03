package org.tendiwa.core;

/**
 * Implementing this interface allows a Module class to create a new World. There should be at least one module in
 * Tendiwa distribution that implements this class, otherwise it won't be possible to create a new world.
 */
public interface WorldProvidingModule {
World createWorld();
}

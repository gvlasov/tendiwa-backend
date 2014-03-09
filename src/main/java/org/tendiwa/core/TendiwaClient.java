package org.tendiwa.core;

public interface TendiwaClient {
/**
 * Called by {@link Tendiwa} on game initialization. Client is initialized after the server is initialized (world loaded
 * and so on).
 *
 * @see Tendiwa#main(String[])
 */

public void startup();
}

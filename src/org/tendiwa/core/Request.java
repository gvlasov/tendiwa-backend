package org.tendiwa.core;

/**
 * Represents an action that client needs the server to do. Requests exist to hide actual method calls from client —
 * otherwise I'd need to make, for example, {@link Character} action methods public so client could call them directly,
 * which is error-inducing.
 */
public interface Request {
public void process();
}

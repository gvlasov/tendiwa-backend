package org.tendiwa.client;

public interface EntityProvider<T> {
public void startEntitySelection(EntitySelectionListener<T> listener);
}

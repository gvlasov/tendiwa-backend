package org.tendiwa.drawing;

public interface DrawableInto {
    <T> void draw(T what, DrawingAlgorithm<? super T> how, TestCanvas.Layer where);

    <T> void draw(T what, DrawingAlgorithm<? super T> how);

    <T> void draw(T what, TestCanvas.Layer where);

    <T> void draw(T what);
}

package org.tendiwa.drawing;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Provides an easy way to create a collection of colors.
 */
@SuppressWarnings("unused")
public class Colors {
    public static List<Color> sequence(int numberOfColors, Function<Integer, Color> colorProducer) {
        List<Color> answer = new ArrayList<>(numberOfColors);
        for (int i = 0; i < numberOfColors; i++) {
            answer.add(colorProducer.apply(i));
        }
        return answer;
    }

    /**
     * Returns an iterator with colors. While iterating it, colors produced by {@code colorProducer} will be
     * picked, for numbers from 0 on.
     *
     * @param colorProducer
     *         A function that maps integers to colors.
     * @return An iterator to go through undefined number of colors defined by some function.
     */
    public static Iterator<Color> infiniteSequence(Function<Integer, Color> colorProducer) {
        return new Iterator<Color>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Color next() {
                return colorProducer.apply(i++);
            }
        };
    }

}

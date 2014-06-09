package org.tendiwa.drawing;

import com.google.common.collect.Iterators;

import java.awt.*;
import java.lang.reflect.Field;
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

    /**
     * Retruns name of a {@link Color} as a String.
     *
     * @param colorParam
     *         A color.
     * @return Color's name.
     */
    public static String colorName(Color colorParam) {
        try {
            // first read all fields in array
            Field[] field = Class.forName("java.awt.Color").getDeclaredFields();
            for (Field f : field) {
                String colorName = f.getName();
                Class<?> t = f.getType();
                // check only for constants - "public static final Color"
                if (t == java.awt.Color.class) {
                    Color defined = (Color) f.get(null);
                    if (defined.equals(colorParam)) {
                        return colorName.toUpperCase();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO_MATCH";
    }

}

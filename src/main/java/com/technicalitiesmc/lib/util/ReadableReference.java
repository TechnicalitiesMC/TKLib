package com.technicalitiesmc.lib.util;

public interface ReadableReference<T> {

    static <T> ReadableReference<T> of(T value) {
        return () -> value;
    }

    T get();

}

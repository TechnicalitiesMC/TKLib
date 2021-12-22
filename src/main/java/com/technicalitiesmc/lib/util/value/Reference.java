package com.technicalitiesmc.lib.util.value;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Reference<T> extends WritableReference<T>, ReadableReference<T> {

    static <T> Reference<T> of(Supplier<T> getter, Consumer<T> setter) {
        return new Reference<T>() {
            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public void set(T value) {
                setter.accept(value);
            }
        };
    }

}

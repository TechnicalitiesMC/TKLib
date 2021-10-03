package com.technicalitiesmc.lib.block;

@FunctionalInterface
public interface BlockComponentConstructor<T> {

    T create(BlockComponentContext context);

}

package com.technicalitiesmc.lib.block;

public interface BlockComponentDataConstructor<T extends BlockComponentData> {

    T create(BlockComponentDataContext context);

}

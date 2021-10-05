package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentContext;
import com.technicalitiesmc.lib.block.BlockComponentData;
import com.technicalitiesmc.lib.block.BlockComponentDataConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

public class BlockData<T extends BlockComponentData> extends BlockComponent.WithData<T> {

    public BlockData(BlockComponentContext context, BlockComponentDataConstructor<T> constructor) {
        super(context, constructor);
    }

    public T get(BlockGetter level, BlockPos pos) {
        return getData(level, pos);
    }

}

package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockData<T extends BlockComponentData> extends BlockComponent.WithData<T> {

    public BlockData(Context context, BlockComponentData.Constructor<T> constructor) {
        super(context, constructor);
    }

    public T at(BlockGetter level, BlockPos pos, BlockState state) {
        return getData(level, pos, state);
    }

}

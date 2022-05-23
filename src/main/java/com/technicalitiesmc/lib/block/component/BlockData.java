package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockData<T extends BlockComponentData> extends BlockComponent.WithData<T> {

    private final boolean serialized;

    private BlockData(Context context, BlockComponentData.Constructor<T> constructor, boolean serialized) {
        super(context, constructor);
        this.serialized = serialized;
    }

    // Static construction

    public static <T extends BlockComponentData> BlockComponent.Constructor<BlockData<T>> of(
            BlockComponentData.Constructor<T> constructor, boolean serialized
    ) {
        return context -> new BlockData<>(context, constructor, serialized);
    }

    public static <T extends BlockComponentData> BlockComponent.Constructor<BlockData<T>> of(
            BlockComponentData.Constructor<T> constructor
    ) {
        return of(constructor, true);
    }

    // API

    @Nullable
    public T at(BlockGetter level, BlockPos pos, BlockState state) {
        return getData(level, pos, state);
    }

    // Implementation


    @Override
    protected boolean isSerialized() {
        return serialized;
    }

}

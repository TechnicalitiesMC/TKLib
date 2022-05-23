package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockRedstoneTrigger extends BlockComponent.WithoutData {

    private final BooleanProperty property;
    private final Callback callback;

    private BlockRedstoneTrigger(Context context, BooleanProperty property, Callback callback) {
        super(context);
        this.property = property;
        this.callback = callback;
    }

    public static Constructor<BlockRedstoneTrigger> of(BooleanProperty property, Callback callback) {
        return ctx -> new BlockRedstoneTrigger(ctx, property, callback);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean moving) {
        var signal = level.hasNeighborSignal(pos);
        if (signal != state.getValue(property)) {
            var newState = state.setValue(property, signal);
            level.setBlock(pos, newState, 3);
            if (signal) {
                callback.onTriggered(level, pos, newState);
            }
        }
    }

    @FunctionalInterface
    public interface Callback {

        void onTriggered(Level level, BlockPos pos, BlockState state);

    }

}

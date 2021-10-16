package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public abstract class BlockComponent {

    final Context context;

    private BlockComponent(Context context) {
        this.context = context;
    }

    protected final TKBlock getBlock() {
        return context.getBlock();
    }

    protected InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return 0;
    }

    public static abstract class WithoutData extends BlockComponent {

        public WithoutData(Context context) {
            super(context);
        }

    }

    public static abstract class WithData<T extends BlockComponentData> extends BlockComponent {

        private final BlockComponentData.Constructor<T> constructor;

        public WithData(Context context, BlockComponentData.Constructor<T> constructor) {
            super(context);
            this.constructor = constructor;
        }

        final T createData(BlockComponentData.Context context) {
            return constructor.create(context);
        }

        protected final T getData(BlockGetter level, BlockPos pos) {
            var entity = Objects.requireNonNull(level.getBlockEntity(pos));
            return ((TKBlockEntity) entity).get(this);
        }

    }

    @FunctionalInterface
    public static interface Constructor<T> {

        T create(Context context);

    }

    public static interface Context {

        TKBlock getBlock();

    }

}

package com.technicalitiesmc.lib.block;

import com.technicalitiesmc.lib.block.multipart.Multipart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract sealed class BlockComponent {

    final Context context;

    private BlockComponent(Context context) {
        this.context = context;
    }

    protected final TKBlock getBlock() {
        return context.getBlock();
    }

    protected BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
        return state;
    }

    public BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean moving) {
    }

    protected InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return 0;
    }

    public static abstract non-sealed class WithoutData extends BlockComponent {

        protected WithoutData(Context context) {
            super(context);
        }

    }

    public static abstract non-sealed class WithData<T extends BlockComponentData> extends BlockComponent {

        private final BlockComponentData.Constructor<T> constructor;

        protected WithData(Context context, BlockComponentData.Constructor<T> constructor) {
            super(context);
            this.constructor = constructor;
        }

        final T createData(BlockComponentData.Context context) {
            return constructor.create(context);
        }

        @Nullable
        protected final T getData(BlockGetter level, BlockPos pos, BlockState state) {
            var entity = Multipart.getBlockEntity(level, pos, state);
            return entity instanceof TKBlockEntity tkEntity ? tkEntity.get(this) : null;
        }

    }

    @FunctionalInterface
    public interface Constructor<T> {

        T create(Context context);

    }

    public interface Context {

        TKBlock getBlock();

    }

}

package com.technicalitiesmc.lib.block;

import com.technicalitiesmc.lib.block.multipart.Multipart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public abstract sealed class BlockComponent {

    final Context context;

    private BlockComponent(Context context) {
        this.context = context;
    }

    protected final TKBlock getBlock() {
        return context.getBlock();
    }

    @Nullable
    protected Object getInterface(Class<?> itf) {
        return null;
    }

    protected BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
        return state;
    }

    protected BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean moving) {
    }

    protected InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    public boolean attack(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState prevState, boolean moving) {
    }

    protected void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return 0;
    }

    @Nullable
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return null;
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state;
    }

    protected BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        return rotate(state, rotation);
    }

    protected InteractionResultHolder<UnaryOperator<BlockState>> rotate(BlockState state, Level level, BlockPos pos, Direction.Axis axis, Rotation rotation) {
        if (axis != Direction.Axis.Y || rotate(state, level, pos, rotation) == state) {
            return new InteractionResultHolder<>(InteractionResult.PASS, UnaryOperator.identity());
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, s -> rotate(state, level, pos, rotation));
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

        protected boolean isSerialized() {
            return true;
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

package com.technicalitiesmc.lib.block.component;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.CustomBlockHighlight;
import com.technicalitiesmc.lib.init.TKLibItemTags;
import com.technicalitiesmc.lib.math.IndexedShape;
import com.technicalitiesmc.lib.math.MergedShape;
import com.technicalitiesmc.lib.math.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockConnections<T extends Comparable<T>> extends BlockComponent.WithoutData {

    private final Map<Direction, ? extends Property<T>> properties;
    private final VoxelShape centerShape;
    private final Table<Direction, T, VoxelShape> sideShapes;
    private final StateGetter<T> stateGetter;
    private final HighlightHandler highlightHandler;

    private BlockConnections(
            Context context,
            Map<Direction, ? extends Property<T>> properties,
            VoxelShape centerShape,
            Table<Direction, T, VoxelShape> sideShapes,
            StateGetter<T> stateGetter,
            @Nullable FineHighlightPredicate fineHighlightPredicate
    ) {
        super(context);
        this.properties = properties;
        this.centerShape = centerShape;
        this.sideShapes = sideShapes;
        this.stateGetter = stateGetter;
        this.highlightHandler = fineHighlightPredicate != null ? new HighlightHandler(fineHighlightPredicate) : HighlightHandler.ALWAYS;
    }

    public static <T extends Comparable<T>> Constructor<BlockConnections<T>> of(
            EnumMap<Direction, ? extends Property<T>> properties,
            VoxelShape centerShape,
            Function<T, VoxelShape> sideShapeFactory,
            StateGetter<T> stateGetter,
            @Nullable FineHighlightPredicate fineHighlightPredicate
    ) {
        var values = properties.values().stream()
                .map(Property::getPossibleValues)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        var sideShapes = HashBasedTable.<Direction, T, VoxelShape>create();
        values.forEach(val -> {
            var baseShape = sideShapeFactory.apply(val);
            if (baseShape != null) {
                for (var side : properties.keySet()) {
                    sideShapes.put(side, val, new IndexedShape(side.ordinal(), ShapeUtils.rotate(baseShape, side)));
                }
            }
        });
        return ctx -> new BlockConnections<>(ctx, properties, centerShape, sideShapes, stateGetter, fineHighlightPredicate);
    }

    public static Constructor<BlockConnections<Boolean>> of(
            EnumMap<Direction, BooleanProperty> properties,
            VoxelShape centerShape,
            VoxelShape sideShape,
            StateGetter<Boolean> stateGetter,
            @Nullable FineHighlightPredicate fineHighlightPredicate
    ) {
        return of(properties, centerShape, b -> b ? sideShape : null, stateGetter, fineHighlightPredicate);
    }

    public VoxelShape shape(BlockState state) {
        var list = new ArrayList<VoxelShape>();
        list.add(centerShape);
        properties.forEach((side, property) -> {
            var value = state.getValue(property);
            var shape = sideShapes.get(side, value);
            if (shape != null) {
                list.add(shape);
            }
        });
        return MergedShape.ofMerged(list);
    }

    @Nullable
    @Override
    protected Object getInterface(Class<?> itf) {
        if (itf == CustomBlockHighlight.class) {
            return highlightHandler;
        }
        return super.getInterface(itf);
    }

    @Override
    protected BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
        if (context.getLevel().isClientSide()) {
            return state;
        }
        for (var entry : properties.entrySet()) {
            var sideState = stateGetter.getState(context.getLevel(), context.getClickedPos(), state, entry.getKey());
            state = state.setValue(entry.getValue(), sideState);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (level.isClientSide()) {
            return state;
        }
        var property = properties.get(side);
        var sideState = stateGetter.getState(level, pos, state, side);
        return state.setValue(property, sideState);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape(state);
    }

    public static boolean isHoldingWrench(Level level, BlockHitResult target, Player player) {
        return player.getMainHandItem().is(TKLibItemTags.WRENCHES);
    }

    private record HighlightHandler(FineHighlightPredicate predicate) implements CustomBlockHighlight {

        public static final HighlightHandler ALWAYS = new HighlightHandler((level, target, player) -> true);

        @Nullable
        @Override
        public VoxelShape getCustomHighlightShape(Level level, BlockHitResult target, Player player) {
            if (predicate.shouldDisplayFineHighlight(level, target, player)) {
                return CustomBlockHighlight.getShapeFromHit(target);
            }
            return null;
        }

    }

    @FunctionalInterface
    public interface StateGetter<T extends Comparable<T>> {

        T getState(BlockGetter level, BlockPos pos, BlockState state, Direction side);

    }

    @FunctionalInterface
    public interface FineHighlightPredicate {

        boolean shouldDisplayFineHighlight(Level level, BlockHitResult target, Player player);

    }

}

package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockHorizontalFacing extends BlockComponent.WithoutData {

    private final Property<Direction> property;

    private BlockHorizontalFacing(Context context, Property<Direction> property) {
        super(context);
        this.property = property;
    }

    // Static construction

    public static BlockComponent.Constructor<BlockHorizontalFacing> of(Property<Direction> property) {
        return context -> new BlockHorizontalFacing(context, property);
    }

    // API

    public Property<Direction> property() {
        return property;
    }

    public Direction front(BlockState state) {
        return state.getValue(property);
    }

    public Direction back(BlockState state) {
        return front(state).getOpposite();
    }

    public Direction left(BlockState state) {
        return front(state).getCounterClockWise();
    }

    public Direction right(BlockState state) {
        return front(state).getClockWise();
    }

    // Impl

    @Override
    protected BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
        return state.setValue(property, context.getHorizontalDirection().getOpposite());
    }

}

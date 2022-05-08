package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockFacing extends BlockComponent.WithoutData {

    private final Property<Direction> property;
    private final boolean towardsPlayer;

    private BlockFacing(Context context, Property<Direction> property, boolean towardsPlayer) {
        super(context);
        this.property = property;
        this.towardsPlayer = towardsPlayer;
    }

    // Static construction

    public static Constructor<BlockFacing> of(Property<Direction> property, boolean towardsPlayer) {
        return context -> new BlockFacing(context, property, towardsPlayer);
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

    // Impl

    @Override
    protected BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
        var lookingDirection = context.getNearestLookingDirection();
        return state.setValue(property, towardsPlayer ? lookingDirection.getOpposite() : lookingDirection);
    }

}

package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockDyeable extends BlockComponent.WithoutData {

    private final EnumProperty<DyeColor> property;

    private BlockDyeable(
            Context context,
            EnumProperty<DyeColor> property
    ) {
        super(context);
        this.property = property;
    }

    public static Constructor<BlockDyeable> of(EnumProperty<DyeColor> property) {
        return ctx -> new BlockDyeable(ctx, property);
    }

    public DyeColor color(BlockState state) {
        return state.getValue(property);
    }

    public BlockState withColor(BlockState state, DyeColor color) {
        return state.setValue(property, color);
    }

    @Override
    protected BlockState getStateForPlacement(BlockPlaceContext context, BlockState state) {
        var player = context.getPlayer();
        if (player != null) {
            var dye = Utils.getDyeColor(player.getOffhandItem());
            if (dye != null) {
                return state.setValue(property, dye);
            }
        }
        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var item = player.getItemInHand(hand);
        if (!item.isEmpty()) {
            var dyeColor = Utils.getDyeColor(item);
            if (dyeColor != null) {
                if (!level.isClientSide()) {
                    level.setBlock(pos, state.setValue(property, dyeColor), 3);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

}

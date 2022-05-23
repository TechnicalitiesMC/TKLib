package com.technicalitiesmc.lib.item.ifo;

import com.technicalitiesmc.lib.init.TKLibIFOs;
import com.technicalitiesmc.lib.util.Utils;
import com.technicalitiesmc.lib.util.Validator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class SelectRangeOverride extends BlockIFOBase {

    private final int maxRange;
    private final Callback callback;

    public SelectRangeOverride(BlockPos origin, int maxRange, Callback callback, Validator validator) {
        super(TKLibIFOs.SELECT_RANGE, origin, validator);
        this.maxRange = maxRange;
        this.callback = callback;
    }

    public SelectRangeOverride(FriendlyByteBuf buf) {
        super(TKLibIFOs.SELECT_RANGE, buf);
        this.maxRange = buf.readVarInt();
        this.callback = null;
    }

    @Override
    public InteractionResult use(Level level, Player player, HitResult hit) {
        if (level.isClientSide() || !(hit instanceof BlockHitResult blockHit)) {
            return InteractionResult.PASS;
        }
        var offset = blockHit.getBlockPos().subtract(origin());
        var magnitude = Utils.getMagnitude(offset);
        var direction = magnitude == 0 || magnitude > maxRange ? blockHit.getDirection() : Utils.getDirection(offset);
        if (magnitude <= maxRange && direction != null) {
            // We are in a straight line of the origin and within range, so check with the callback
            if (callback.onRangeSelected(level, origin(), player, direction, magnitude)) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.CONSUME_PARTIAL;
    }

    @Override
    public InteractionResult hit(Level level, Player player, HitResult hit) {
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public Component getTip(Level level, Player player) {
        return new TextComponent("Select a block up to " + maxRange + " blocks away in a straight line.");
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        super.writeToNetwork(buf);
        buf.writeVarInt(maxRange);
    }

    @FunctionalInterface
    public interface Callback {

        boolean onRangeSelected(Level level, BlockPos origin, Player player, Direction direction, int range);

    }

}
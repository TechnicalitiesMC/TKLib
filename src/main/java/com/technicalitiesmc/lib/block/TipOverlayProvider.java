package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public interface TipOverlayProvider {

    @Nullable
    Overlay buildOverlay(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, ModifierKeys modifiers);

    record ModifierKeys(boolean shift, boolean ctrl, boolean alt) {
    }

    record Overlay(List<Component> lines) {
    }

}

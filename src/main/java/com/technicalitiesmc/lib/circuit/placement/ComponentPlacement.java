package com.technicalitiesmc.lib.circuit.placement;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.technicalitiesmc.lib.circuit.component.ComponentState;
import com.technicalitiesmc.lib.math.VecDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public interface ComponentPlacement {

    Instance begin();

    Instance deserialize(FriendlyByteBuf buf);

    interface Instance {

        @Nullable
        default VoxelShape createOverrideShape(PlacementContext.Client context, Vec3i clickedPos, VecDirection clickedFace, HitResult hit) {
            return null;
        }

        boolean tick(PlacementContext.Client context, Vec3i clickedPos, VecDirection clickedFace);

        void stopPlacing(PlacementContext.Client context);

        boolean isValid(PlacementContext.Client context);

        void serialize(FriendlyByteBuf buf);

        void place(PlacementContext.Server context);

        default Multimap<Vec3i, ComponentState> getPreviewStates(Player player) {
            return ImmutableMultimap.of();
        }

    }

}

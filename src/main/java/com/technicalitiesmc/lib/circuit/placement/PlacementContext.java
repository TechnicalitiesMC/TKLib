package com.technicalitiesmc.lib.circuit.placement;

import com.technicalitiesmc.lib.circuit.component.ComponentSlot;
import com.technicalitiesmc.lib.circuit.component.ComponentState;
import com.technicalitiesmc.lib.circuit.component.ComponentType;
import com.technicalitiesmc.lib.math.VecDirection;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface PlacementContext {

    Player getPlayer();

    InteractionHand getHand();

    VecDirection getFacing();

    VecDirection getHorizontalFacing();

    interface Client extends PlacementContext {

        @Nullable
        ComponentState get(Vec3i pos, ComponentSlot slot);

        boolean canPlace(Vec3i pos, ComponentType type);

        boolean isTopSolid(Vec3i pos);

        boolean isWithinBounds(Vec3i pos);

        boolean isModifierPressed();

    }

    interface Server {

        default boolean tryPut(Vec3i pos, ComponentType type) {
            return tryPut(pos, type, type::create);
        }

        boolean tryPut(Vec3i pos, ComponentType type, ComponentType.Factory factory);

        boolean tryPutAll(Predicate<MultiPlacementContext> function);

        void consumeItems(int count);

        void playSound();

    }

    interface MultiPlacementContext {

        default boolean at(Vec3i pos, ComponentType type) {
            return at(pos, type, type::create);
        }

        boolean at(Vec3i pos, ComponentType type, ComponentType.Factory factory);

    }

}

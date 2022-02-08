package com.technicalitiesmc.lib.circuit.component;

import com.mojang.math.Vector3f;
import com.technicalitiesmc.lib.math.VecDirection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.function.Supplier;

public abstract class ClientComponent {

    public static ClientComponent base(Supplier<ItemStack> itemSupplier){
        return new ClientComponent() {
            @Override
            public ItemStack getPickedItem(ComponentState state) {
                return itemSupplier.get();
            }
        };
    }

    public AABB getBoundingBox(ComponentState state) {
        return CircuitComponent.FULL_BLOCK;
    }

    public abstract ItemStack getPickedItem(ComponentState state);

    public void onPicking(ComponentState state, Player player) {
    }

    public InteractionResult use(ComponentState state, Player player, InteractionHand hand, VecDirection sideHit, Vector3f hit) {
        return InteractionResult.PASS;
    }

    public boolean isTopSolid(ComponentState state) {
        return false;
    }

    public int getTint(ComponentState state, int tintIndex) {
        return 0xFFFFFFFF;
    }

}

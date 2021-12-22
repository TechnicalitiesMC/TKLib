package com.technicalitiesmc.lib.circuit.component;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface ComponentHarvestContext {

    static ComponentHarvestContext dummy(ServerLevel level) {
        return new ComponentHarvestContext() {

            @Override
            public ServerLevel getLevel() {
                return level;
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public void drop(ItemStack item) {
            }

        };
    }

    static ComponentHarvestContext forPlayer(Player player) {
        return new ComponentHarvestContext() {

            @Override
            public ServerLevel getLevel() {
                return (ServerLevel) player.getLevel();
            }

            @Override
            public boolean isCreative() {
                return player.isCreative();
            }

            @Override
            public void drop(ItemStack item) {
                var copy = item.copy();
                player.addItem(copy);
                if (!copy.isEmpty()) {
                    player.level.addFreshEntity(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), copy));
                }
            }

        };
    }

    static ComponentHarvestContext drop(ServerLevel level, Consumer<ItemStack> dropper) {
        return new ComponentHarvestContext() {

            @Override
            public ServerLevel getLevel() {
                return level;
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public void drop(ItemStack item) {
                dropper.accept(item);
            }

        };
    }

    ServerLevel getLevel();

    boolean isCreative();

    void drop(ItemStack item);

}

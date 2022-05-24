package com.technicalitiesmc.lib.menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class SimpleMenu extends TKMenu {

    private final ContainerLevelAccess levelAccess;
    private final List<RegistryObject<Block>> blocks;

    protected SimpleMenu(
            RegistryObject<? extends MenuType<?>> type, int id, Inventory playerInv,
            ContainerLevelAccess levelAccess, RegistryObject<Block> block,
            ResourceLocation texture, int width, int height
    ) {
        this(type, id, playerInv, levelAccess, List.of(block), texture, width, height);
    }

    protected SimpleMenu(
            RegistryObject<? extends MenuType<?>> type, int id, Inventory playerInv,
            ContainerLevelAccess levelAccess, List<RegistryObject<Block>> blocks,
            ResourceLocation texture, int width, int height
    ) {
        super(type, id, playerInv, texture, width, height);
        this.levelAccess = levelAccess;
        this.blocks = blocks;
    }

    @Override
    public boolean stillValid(Player player) {
        for (var block : blocks) {
            if (stillValid(levelAccess, player, block.get())) {
                return true;
            }
        }
        return false;
    }

}

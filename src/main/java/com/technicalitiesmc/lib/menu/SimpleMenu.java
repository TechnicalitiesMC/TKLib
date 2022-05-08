package com.technicalitiesmc.lib.menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class SimpleMenu extends TKMenu {

    private final ContainerLevelAccess levelAccess;
    private final RegistryObject<Block> block;

    protected SimpleMenu(
            RegistryObject<? extends MenuType<?>> type, int id, Inventory playerInv,
            ContainerLevelAccess levelAccess, RegistryObject<Block> block,
            ResourceLocation texture, int width, int height
    ) {
        super(type, id, playerInv, texture, width, height);
        this.levelAccess = levelAccess;
        this.block = block;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, block.get());
    }

}

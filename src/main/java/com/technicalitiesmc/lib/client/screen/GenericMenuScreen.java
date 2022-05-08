package com.technicalitiesmc.lib.client.screen;

import com.technicalitiesmc.lib.menu.TKMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GenericMenuScreen<T extends TKMenu> extends TKMenuScreen<T> {

    public GenericMenuScreen(T menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, menu.texture(), menu.width(), menu.height());
    }

}

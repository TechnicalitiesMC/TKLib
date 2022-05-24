package com.technicalitiesmc.lib.menu.component;

import com.technicalitiesmc.lib.client.screen.widget.DynamicStringWidget;
import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.menu.MenuComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class DynamicStringComponent extends MenuComponent {

    private final int x, y, width;
    private final Supplier<Component> text;

    public DynamicStringComponent(int x, int y, int width, Supplier<Component> text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.text = text;
    }

    @Override
    public Supplier<Widget> widgetSupplier() {
        return () -> new DynamicStringWidget(x, y, width, this::isEnabled, text);
    }

    @Override
    public void subscribe(DataTracker tracker) {
    }

    @Override
    public void onEvent(FriendlyByteBuf buf) {
    }

}

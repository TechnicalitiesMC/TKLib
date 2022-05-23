package com.technicalitiesmc.lib.menu.component;

import com.technicalitiesmc.lib.client.screen.widget.EnumSelectorWidget;
import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.menu.MenuComponent;
import com.technicalitiesmc.lib.util.TooltipEnabled;
import com.technicalitiesmc.lib.util.value.Reference;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnumSelectorComponent<E extends Enum<E>> extends MenuComponent {

    private final int x, y, width, height, u, v;
    private final Reference<E> reference;
    private final List<E> values;
    private final E defaultValue;
    @Nullable
    private final Function<E, TooltipEnabled> tooltipProvider;

    public EnumSelectorComponent(int x, int y, int width, int height, int u, int v,
                                 Reference<E> reference, List<E> values, E defaultValue,
                                 @Nullable Function<E, TooltipEnabled> tooltipProvider) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.reference = reference;
        this.values = values;
        this.defaultValue = defaultValue;
        this.tooltipProvider = tooltipProvider;
    }

    public EnumSelectorComponent(int x, int y, int width, int height, int u, int v,
                                 Reference<E> reference, E defaultValue,
                                 @Nullable Function<E, TooltipEnabled> tooltipProvider) {
        this(x, y, width, height, u, v, reference, (List) List.of(reference.get().getClass().getEnumConstants()), defaultValue, tooltipProvider);
    }

    @Override
    public Supplier<Widget> widgetSupplier() {
        return () -> new EnumSelectorWidget<>(
                x, y, width, height, u, v,
                Reference.of(reference::get, this::setAndNotify),
                values,
                defaultValue,
                tooltipProvider
        );
    }

    @Override
    public void subscribe(DataTracker tracker) {
        tracker.trackEnum(reference);
    }

    @Override
    public void onEvent(FriendlyByteBuf buf) {
        reference.set(buf.readEnum((Class<E>) reference.get().getClass()));
    }

    private void setAndNotify(E value) {
        reference.set(value);
        notifyServer(buf -> {
            buf.writeEnum(value);
        });
    }

}

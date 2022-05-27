package com.technicalitiesmc.lib.menu.component;

import com.technicalitiesmc.lib.client.screen.widget.NumberSelectorWidget;
import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.menu.MenuComponent;
import com.technicalitiesmc.lib.util.TooltipProvider;
import com.technicalitiesmc.lib.util.value.Reference;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class NumberSelectorComponent extends MenuComponent {

    private final int x, y, width, height;
    private final Reference<Integer> reference;
    private final Range<Integer> range;
    @Nullable
    private final TooltipProvider tooltipProvider;

    public NumberSelectorComponent(int x, int y, int width, int height,
                                   Reference<Integer> reference, Range<Integer> range,
                                   @Nullable TooltipProvider tooltipProvider) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.reference = reference;
        this.range = range;
        this.tooltipProvider = tooltipProvider;
    }

    @Override
    public Supplier<Widget> widgetSupplier() {
        return () -> new NumberSelectorWidget(
                x, y, width, height, this::isEnabled,
                Reference.of(reference::get, this::setAndNotify),
                range,
                tooltipProvider
        );
    }

    @Override
    public void subscribe(DataTracker tracker) {
        tracker.trackInt(reference);
    }

    @Override
    public void onEvent(FriendlyByteBuf buf) {
        reference.set(buf.readVarInt());
    }

    private void setAndNotify(int value) {
        reference.set(value);
        notifyServer(buf -> {
            buf.writeVarInt(value);
        }, 10);
    }

}

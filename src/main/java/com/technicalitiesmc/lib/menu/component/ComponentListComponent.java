package com.technicalitiesmc.lib.menu.component;

import com.technicalitiesmc.lib.client.screen.widget.ListWidget;
import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.menu.MenuComponent;
import com.technicalitiesmc.lib.util.value.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public class ComponentListComponent extends MenuComponent {

    private final int x, y, width, height;
    private final Iterable<? extends Component> entries;
    private final Reference<Integer> selectedEntry;

    public ComponentListComponent(int x, int y, int width, int height, Iterable<? extends Component> entries, Reference<Integer> selectedEntry) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.entries = entries;
        this.selectedEntry = selectedEntry;
    }

    @Override
    public Supplier<Widget> widgetSupplier() {
        return () -> new ListWidget<>(
                x, y, width, height, this::isEnabled,
                () -> StreamSupport.stream(entries.spliterator(), false).map(ListWidget.ComponentEntry::new).iterator(),
                Reference.of(selectedEntry::get, this::setAndNotify),
                ListWidget.ComponentEntry.HEIGHT
        );
    }

    @Override
    public void subscribe(DataTracker tracker) {
        tracker.trackInt(selectedEntry);
    }

    @Override
    public void onEvent(FriendlyByteBuf buf) {
        var value = buf.readVarInt();
        selectedEntry.set(value);
    }

    private void setAndNotify(int value) {
        selectedEntry.set(value);
        notifyServer(buf -> {
            buf.writeVarInt(value);
        });
    }

}

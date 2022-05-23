package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import com.technicalitiesmc.lib.util.value.Reference;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MenuComponent {

    int id;

    public abstract Supplier<Widget> widgetSupplier();

    public abstract void subscribe(DataTracker tracker);

    public abstract void onEvent(FriendlyByteBuf buf);

    protected final void notifyServer(Consumer<FriendlyByteBuf> writer) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        writer.accept(buf);
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        TKLibNetworkHandler.sendMenuComponentMessage(id, bytes);
    }

    public interface DataTracker {

        void trackInts(int[] array);

        void trackInt(Reference<Integer> reference);

        void trackEnum(Reference<? extends Enum<?>> reference);

    }

}

package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import com.technicalitiesmc.lib.util.value.Reference;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MenuComponent {

    private static final BooleanSupplier ALWAYS = () -> true;
    private static final BooleanSupplier NEVER = () -> false;

    int id;
    private BooleanSupplier enabled = () -> true;
    private int delayUntilSend = -1;
    private byte[] bytesToSend = null;

    public abstract Supplier<Widget> widgetSupplier();

    public abstract void subscribe(DataTracker tracker);

    public abstract void onEvent(FriendlyByteBuf buf);

    public void clientTick() {
        if (delayUntilSend >= 0) {
            if (delayUntilSend-- == 0) {
                TKLibNetworkHandler.sendServerboundMenuComponentMessage(id, bytesToSend);
                bytesToSend = null;
            }
        }
    }

    public void onClientClosed() {
        if (delayUntilSend >= 0) {
            TKLibNetworkHandler.sendServerboundMenuComponentMessage(id, bytesToSend);
            bytesToSend = null;
        }
    }

    protected final void notifyServer(Consumer<FriendlyByteBuf> writer) {
        notifyServer(writer, 0);
    }

    protected final void notifyServer(Consumer<FriendlyByteBuf> writer, int delay) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        writer.accept(buf);
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        if (delay == 0) {
            TKLibNetworkHandler.sendServerboundMenuComponentMessage(id, bytes);
        } else {
            delayUntilSend = delay;
            bytesToSend = bytes;
        }
    }

    public final boolean isEnabled() {
        return enabled.getAsBoolean();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled ? ALWAYS : NEVER;
    }

    public void setEnabledWhen(BooleanSupplier supplier) {
        this.enabled = supplier;
    }

    public interface DataTracker {

        void trackInts(int[] array);

        void trackBoolean(Reference<Boolean> reference);

        void trackInt(Reference<Integer> reference);

        void trackEnum(Reference<? extends Enum<?>> reference);

    }

}

package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.menu.TKMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundMenuComponentMessagePacket implements Packet {

    private final int component;
    private final byte[] data;

    public ServerboundMenuComponentMessagePacket(int component, byte[] data) {
        this.component = component;
        this.data = data;
    }

    public ServerboundMenuComponentMessagePacket(FriendlyByteBuf buf) {
        this.component = buf.readInt();
        this.data = buf.readByteArray();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(component);
        buf.writeByteArray(data);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            var menu = player.containerMenu;
            if (menu instanceof TKMenu tkm) {
                tkm.onMessage(component, data);
            }
        });
        return true;
    }

}

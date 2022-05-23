package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.item.ifo.IFOManager;
import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundDisableIFOPacket implements Packet {

    public ClientboundDisableIFOPacket() {
    }

    public ClientboundDisableIFOPacket(FriendlyByteBuf buf) {
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = Utils.getClientPlayer();
            if (player != null) {
                IFOManager.disable(player);
            }
        });
        return true;
    }

}

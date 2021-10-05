package com.technicalitiesmc.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public interface Packet {

    boolean handle(NetworkEvent.Context context);

    void encode(FriendlyByteBuf buf);

}

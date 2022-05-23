package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.init.TKLibIFOs;
import com.technicalitiesmc.lib.item.ifo.IFOManager;
import com.technicalitiesmc.lib.item.ifo.ItemFunctionalOverride;
import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundEnableIFOPacket implements Packet {

    private final ItemFunctionalOverride ifo;

    public ClientboundEnableIFOPacket(ItemFunctionalOverride ifo) {
        this.ifo = ifo;
    }

    public ClientboundEnableIFOPacket(FriendlyByteBuf buf) {
        var key = new ResourceLocation(buf.readUtf());
        var type = TKLibIFOs.FORGE_REGISTRY.get().getValue(key);
        this.ifo = type.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(ifo.type().getRegistryName().toString());
        ifo.writeToNetwork(buf);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = Utils.getClientPlayer();
            if (player != null) {
                IFOManager.enable(player, ifo);
            }
        });
        return true;
    }

}

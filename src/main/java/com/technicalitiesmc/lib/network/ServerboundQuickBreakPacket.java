package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.TKLibEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundQuickBreakPacket implements Packet {

    private final BlockPos pos;

    public ServerboundQuickBreakPacket(BlockPos pos) {
        this.pos = pos;
    }

    public ServerboundQuickBreakPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            TKLibEventHandler.quickBreak(player, pos);
        });
        return true;
    }

}

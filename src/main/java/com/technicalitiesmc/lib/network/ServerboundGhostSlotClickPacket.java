package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.menu.slot.TKGhostSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundGhostSlotClickPacket implements Packet {

    private final int slotNumber;

    public ServerboundGhostSlotClickPacket(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public ServerboundGhostSlotClickPacket(FriendlyByteBuf buf) {
        this.slotNumber = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slotNumber);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            var menu = player.containerMenu;
            var slot = menu.slots.get(slotNumber);
            if (slot instanceof TKGhostSlot) {
                var carried = menu.getCarried();
                var newItem = carried.copy();
                newItem.setCount(Math.min(newItem.getCount(), slot.getMaxStackSize(carried)));
                slot.set(newItem);
            }
        });
        return true;
    }

}

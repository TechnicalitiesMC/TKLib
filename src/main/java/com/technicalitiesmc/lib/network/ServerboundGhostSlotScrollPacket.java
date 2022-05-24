package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.menu.slot.TKGhostSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundGhostSlotScrollPacket implements Packet {

    private final int slotNumber, amount;

    public ServerboundGhostSlotScrollPacket(int slotNumber, int amount) {
        this.slotNumber = slotNumber;
        this.amount = amount;
    }

    public ServerboundGhostSlotScrollPacket(FriendlyByteBuf buf) {
        this.slotNumber = buf.readInt();
        this.amount = buf.readVarInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slotNumber);
        buf.writeVarInt(amount);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            var menu = player.containerMenu;
            var slot = menu.slots.get(slotNumber);
            if (slot instanceof TKGhostSlot) {
                var stack = slot.getItem();
                if (!stack.isEmpty()) {
                    stack.setCount(Math.max(1, Math.min(stack.getCount() + amount, slot.getMaxStackSize())));
                    slot.set(stack);
                }
            }
        });
        return true;
    }

}

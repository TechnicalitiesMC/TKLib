package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.menu.TKGhostSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ServerboundGhostSlotClickPacket implements Packet {

    private final int slotNumber;
    private final ItemStack stack;

    public ServerboundGhostSlotClickPacket(int slotNumber, ItemStack stack) {
        this.slotNumber = slotNumber;
        this.stack = stack;
    }

    public ServerboundGhostSlotClickPacket(FriendlyByteBuf buf) {
        this.slotNumber = buf.readInt();
        this.stack = buf.readItem();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slotNumber);
        buf.writeItem(stack);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            var slot = player.containerMenu.slots.get(slotNumber);
            if (slot instanceof TKGhostSlot) {
                slot.set(stack.copy().split(slot.getMaxStackSize(stack)));
            }
        });
        return true;
    }

}

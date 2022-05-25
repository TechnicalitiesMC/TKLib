package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.menu.slot.TKGhostSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundGhostSlotSetPacket implements Packet {

    private final int slotNumber;
    private final ItemStack stack;

    public ServerboundGhostSlotSetPacket(int slotNumber, ItemStack stack) {
        this.slotNumber = slotNumber;
        this.stack = stack;
    }

    public ServerboundGhostSlotSetPacket(FriendlyByteBuf buf) {
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
            var menu = player.containerMenu;
            var slot = menu.slots.get(slotNumber);
            if (slot instanceof TKGhostSlot) {
                stack.setCount(Math.min(stack.getCount(), slot.getMaxStackSize(stack)));
                slot.set(stack);
            }
        });
        return true;
    }

}

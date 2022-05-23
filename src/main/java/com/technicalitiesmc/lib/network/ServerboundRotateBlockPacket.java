package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.block.RotationHandler;
import com.technicalitiesmc.lib.block.TKBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.network.NetworkEvent;

public class ServerboundRotateBlockPacket implements Packet {

    private final BlockPos pos;
    private final Direction.Axis axis;
    private final Rotation rotation;

    public ServerboundRotateBlockPacket(BlockPos pos, Direction.Axis axis, Rotation rotation) {
        this.pos = pos;
        this.axis = axis;
        this.rotation = rotation;
    }

    public ServerboundRotateBlockPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.axis = buf.readEnum(Direction.Axis.class);
        this.rotation = buf.readEnum(Rotation.class);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(axis);
        buf.writeEnum(rotation);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            var level = player.getLevel();
            var state = level.getBlockState(pos);
            var rotationHandler = TKBlock.getInterface(state.getBlock(), RotationHandler.class);
            if (rotationHandler != null) {
                rotationHandler.rotate(state, level, pos, axis, rotation);
            }
        });
        return true;
    }

}

package com.technicalitiesmc.lib.network;

import com.technicalitiesmc.lib.TKLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class TKLibNetworkHandler {

    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(TKLib.MODID, "main"),
                () -> "1.0",
                s -> true,
                s -> true);

        register(ServerboundGhostSlotClickPacket.class, ServerboundGhostSlotClickPacket::new);
        register(ServerboundMenuComponentMessagePacket.class, ServerboundMenuComponentMessagePacket::new);
        register(ServerboundRotateBlockPacket.class, ServerboundRotateBlockPacket::new);
    }

    private static <T extends Packet> void register(Class<T> type, Function<FriendlyByteBuf, T> decoder) {
        INSTANCE.messageBuilder(type, ID++)
                .encoder(Packet::encode)
                .decoder(decoder)
                .consumer((first, second) -> first.handle(second.get()) || false)
                .add();
    }

    private static void sendToClient(Packet packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static void sendToServer(Packet packet) {
        INSTANCE.sendToServer(packet);
    }

    public static void sendServerboundGhostSlotClick(int slotNumber) {
        sendToServer(new ServerboundGhostSlotClickPacket(slotNumber));
    }

    public static void sendMenuComponentMessage(int component, byte[] data) {
        sendToServer(new ServerboundMenuComponentMessagePacket(component, data));
    }

    public static void sendServerboundRotateBlock(BlockPos pos, Direction.Axis axis, Rotation rotation) {
        sendToServer(new ServerboundRotateBlockPacket(pos, axis, rotation));
    }

}

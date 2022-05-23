package com.technicalitiesmc.lib.item.ifo;

import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

public class IFOManager {

    private static final Capability<IFOStorage> IFO_STORAGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(IFOManager::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(IFOManager::onUseItemOnBlock);
    }

    @Nullable
    private static IFOStorage getStorage(Player player) {
        if (player instanceof FakePlayer) {
            return null;
        }
        return player.getCapability(IFO_STORAGE_CAPABILITY).orElse(null);
    }

    public static void enable(Player player, ItemFunctionalOverride override) {
        var storage = getStorage(player);
        if (storage == null) {
            return;
        }
        storage.setActiveOverride(override);
        if (!player.getLevel().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            TKLibNetworkHandler.sendClientboundEnableIFO(serverPlayer, override);
        }
    }

    public static void disable(Player player) {
        var storage = getStorage(player);
        if (storage == null) {
            return;
        }
        storage.setActiveOverride(null);
        if (!player.getLevel().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            TKLibNetworkHandler.sendClientboundDisableIFO(serverPlayer);
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        var player = event.player;
        var storage = getStorage(player);
        if (storage == null || storage.getActiveOverride() == null) {
            return;
        }
        var override = storage.getActiveOverride();

        if (player.getLevel().isClientSide()) {
            if (!override.isValid(player.getLevel(), player)) {
                // This IFO is invalid
                disable(player);
            }
        } else {
            var tip = override.getTip(player.getLevel(), player);
            if (tip != null) {
                player.displayClientMessage(tip, true);
            }
        }
    }

    private static void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getPlayer();
        var storage = getStorage(player);
        if (storage == null || storage.getActiveOverride() == null) {
            return;
        }
        var override = storage.getActiveOverride();

        var result = override.use(player.getLevel(), player, event.getHitVec());
        if (!player.getLevel().isClientSide() && (result == InteractionResult.SUCCESS || result == InteractionResult.FAIL)) {
            // This IFO has completed
            disable(player);
        }
        if (result.consumesAction()) {
            player.swing(InteractionHand.MAIN_HAND);
        }

        event.setCanceled(true);
        event.setCancellationResult(result == InteractionResult.FAIL ?
                InteractionResult.FAIL : InteractionResult.sidedSuccess(player.getLevel().isClientSide()));
    }

}

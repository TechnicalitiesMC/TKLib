package com.technicalitiesmc.lib;

import com.technicalitiesmc.lib.init.TKLibBlockTags;
import com.technicalitiesmc.lib.init.TKLibItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TKLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TKLibEventHandler {

    private static final ThreadLocal<Player> COLLECT_DROPS_TARGET = new ThreadLocal<>();

    public static boolean validate(Player player, BlockState state) {
        return player.isShiftKeyDown() &&
                state.is(TKLibBlockTags.WRENCH_BREAKS_INSTANTLY) &&
                player.getMainHandItem().is(TKLibItemTags.TOOLS_WRENCH);
    }

    public static void quickBreak(ServerPlayer player, BlockPos pos) {
        var state = player.getLevel().getBlockState(pos);
        if (validate(player, state)) {
            player.getLevel().levelEvent(player, 2001, pos, Block.getId(state));
            player.gameMode.destroyBlock(pos);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (validate(event.getPlayer(), event.getTargetBlock())) {
            event.setCanHarvest(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        var player = event.getPlayer();
        if (validate(player, event.getState())) {
            // destroy progress = break speed / block destroy speed / 30
            // break speed = destroy progress * block destroy speed * 30
            var blockDestroySpeed = event.getState().getDestroySpeed(player.getLevel(), event.getPos());
            event.setNewSpeed(0.98F * blockDestroySpeed * 30);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBeginBlockBreak(BlockEvent.BreakEvent event) {
        var player = event.getPlayer();
        if (validate(player, event.getState())) {
            COLLECT_DROPS_TARGET.set(player);
        }
    }

    public static void onStopBreaking() {
        COLLECT_DROPS_TARGET.set(null);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            var player = COLLECT_DROPS_TARGET.get();
            if (player != null) {
                var item = itemEntity.getItem().copy();
                if (!player.getInventory().add(item)) {
                    itemEntity.setItem(item);
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onUseItem(PlayerInteractEvent.RightClickBlock event) {
        if (event.getPlayer().isShiftKeyDown() && event.getItemStack().is(TKLibItemTags.TOOLS_WRENCH)) {
            var state = event.getWorld().getBlockState(event.getPos());
            if (state.is(TKLibBlockTags.WRENCH_SNEAK_COMPATIBLE)) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }

}

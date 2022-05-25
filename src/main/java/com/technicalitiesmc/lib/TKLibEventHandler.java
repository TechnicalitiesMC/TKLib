package com.technicalitiesmc.lib;

import com.technicalitiesmc.lib.init.TKLibBlockTags;
import com.technicalitiesmc.lib.init.TKLibItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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

    private static boolean validate(Player player, BlockState state) {
        return player.isShiftKeyDown() &&
                state.is(TKLibBlockTags.WRENCH_BREAKS_INSTANTLY) &&
                player.getMainHandItem().is(TKLibItemTags.TOOLS_WRENCH);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (validate(event.getPlayer(), event.getTargetBlock())) {
            event.setCanHarvest(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (validate(event.getPlayer(), event.getState())) {
            // destroy progress = break speed / block destroy speed / 30
            // break speed = destroy progress * block destroy speed * 30
            var blockDestroySpeed = event.getState().getDestroySpeed(event.getPlayer().level, event.getPos());
            event.setNewSpeed(0.98F * blockDestroySpeed * 30);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBeginBlockBreak(BlockEvent.BreakEvent event) {
        if (validate(event.getPlayer(), event.getState())) {
            COLLECT_DROPS_TARGET.set(event.getPlayer());
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

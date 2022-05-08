package com.technicalitiesmc.lib.client;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.block.CustomBlockHighlight;
import com.technicalitiesmc.lib.block.TKBlock;
import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TKLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TKLibClientEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDrawBlockHighlight(DrawSelectionEvent.HighlightBlock event) {
        var mc = Minecraft.getInstance();
        var target = event.getTarget();

        var state = Utils.resolveHit(mc.level, target);
        var cbh = TKBlock.getInterface(state.getBlock(), CustomBlockHighlight.class);
        if (cbh == null) {
            return;
        }

        var shape = cbh.getCustomHighlightShape(mc.level, target, mc.player);
        if (shape == null) {
            return;
        }

        if (!shape.isEmpty()) {
            var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            var offset = Vec3.atLowerCornerOf(target.getBlockPos()).subtract(cameraPos);
            LevelRenderer.renderShape(
                    event.getPoseStack(),
                    event.getMultiBufferSource().getBuffer(RenderType.lines()),
                    shape,
                    offset.x, offset.y, offset.z,
                    0, 0, 0, 0.4f
            );
        }

        event.setCanceled(true);
    }

}

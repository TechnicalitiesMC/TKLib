package com.technicalitiesmc.lib.client;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.math.IndexedBlockHitResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TKLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TKLibClientEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDrawBlockHighlight(DrawSelectionEvent.HighlightBlock event) {
        var target = event.getTarget();
        VoxelShape shape = null;
        while (target instanceof IndexedBlockHitResult hit) {
            target = hit.getParent();
            shape = hit.getShape();
        }
        if (shape == null) {
            return;
        }

        var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        var offset = Vec3.atLowerCornerOf(target.getBlockPos()).subtract(cameraPos);
        LevelRenderer.renderShape(
                event.getPoseStack(),
                event.getMultiBufferSource().getBuffer(RenderType.lines()),
                shape,
                offset.x, offset.y, offset.z,
                0, 0, 0, 0.4f
        );

        event.setCanceled(true);
    }

}

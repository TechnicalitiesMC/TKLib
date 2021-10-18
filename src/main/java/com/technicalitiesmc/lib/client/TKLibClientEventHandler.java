package com.technicalitiesmc.lib.client;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.math.IndexedBlockHitResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TKLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TKLibClientEventHandler {

    @SubscribeEvent
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

        // TODO: Use an AT to expose LevelRenderer#renderShape(...)

        var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        var offset = Vec3.atLowerCornerOf(target.getBlockPos()).subtract(cameraPos);

        var pose = event.getMatrix().last();
        var buffer = event.getBuffers().getBuffer(RenderType.lines());
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float f = (float) (maxX - minX);
            float f1 = (float) (maxY - minY);
            float f2 = (float) (maxZ - minZ);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f = f / f3;
            f1 = f1 / f3;
            f2 = f2 / f3;
            buffer.vertex(pose.pose(), (float) (minX + offset.x), (float) (minY + offset.y), (float) (minZ + offset.z))
                    .color(0, 0, 0, 0.4f)
                    .normal(pose.normal(), f, f1, f2)
                    .endVertex();
            buffer.vertex(pose.pose(), (float) (maxX + offset.x), (float) (maxY + offset.y), (float) (maxZ + offset.z))
                    .color(0, 0, 0, 0.4f)
                    .normal(pose.normal(), f, f1, f2)
                    .endVertex();
        });

        event.setCanceled(true);
    }

}

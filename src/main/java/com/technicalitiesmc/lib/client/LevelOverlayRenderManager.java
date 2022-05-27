package com.technicalitiesmc.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelOverlayRenderManager {

    private static final List<LevelOverlayRenderer> RENDERERS = new ArrayList<>();

    public static void register(LevelOverlayRenderer renderer) {
        RENDERERS.add(renderer);
    }

    public static void renderLevelOverlays(ClientLevel level, Player player, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Camera camera, Frustum frustum, float partialTick) {
        for (var renderer : RENDERERS) {
            renderer.render(level, player, bufferSource, poseStack, camera, frustum, partialTick);
        }
    }

}

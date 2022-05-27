package com.technicalitiesmc.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.player.Player;

public interface LevelOverlayRenderer {

    void render(ClientLevel level, Player player, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, Camera camera, Frustum frustum, float partialTick);

}

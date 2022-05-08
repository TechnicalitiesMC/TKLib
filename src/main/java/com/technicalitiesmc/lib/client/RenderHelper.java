package com.technicalitiesmc.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;

public class RenderHelper {

    public static void renderCuboid(AABB aabb, PoseStack poseStack, VertexConsumer buffer, int light, float r, float g, float b, float a) {
        var mat4 = poseStack.last().pose();
        var mat3 = poseStack.last().normal();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, -1.0F, 0.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 1.0F, 0.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, -1.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, -1.0F, 0.0F, 0.0F).endVertex();

        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(r, g, b, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(r, g, b, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(r, g, b, a).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
        buffer.vertex(mat4, (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(r, g, b, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 1.0F, 0.0F, 0.0F).endVertex();
    }

    public static void renderQuad(float minX, float minY, float maxX, float maxY, float z, PoseStack poseStack, VertexConsumer buffer, int light, float r, float g, float b, float a) {
        var mat4 = poseStack.last().pose();
        var mat3 = poseStack.last().normal();

        buffer.vertex(mat4, minX, minY, z).color(r, g, b, a).uv(0.0F, 0.0F).uv2(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, maxX, minY, z).color(r, g, b, a).uv(1.0F, 0.0F).uv2(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, maxX, maxY, z).color(r, g, b, a).uv(1.0F, 1.0F).uv2(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
        buffer.vertex(mat4, minX, maxY, z).color(r, g, b, a).uv(0.0F, 1.0F).uv2(OverlayTexture.NO_OVERLAY).uv2(light).normal(mat3, 0.0F, 0.0F, 1.0F).endVertex();
    }

}

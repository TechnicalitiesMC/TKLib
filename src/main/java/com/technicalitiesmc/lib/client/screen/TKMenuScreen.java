package com.technicalitiesmc.lib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.menu.ColoredSlot;
import com.technicalitiesmc.lib.menu.TKGhostSlot;
import com.technicalitiesmc.lib.menu.TKMenu;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TKMenuScreen<T extends TKMenu> extends AbstractContainerScreen<T> {

    private final ResourceLocation background;

    public TKMenuScreen(T menu, Inventory playerInv, Component title, ResourceLocation background) {
        super(menu, playerInv, title);
        this.background = background;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, background);
        blit(poseStack, leftPos, (height - imageHeight) / 2, 0, 0, imageWidth, imageHeight);

        for (var slot : menu.slots) {
            if (!(slot instanceof ColoredSlot s)) {
                continue;
            }
            int color = s.getColor();
            if (color == 0) {
                continue;
            }
            fill(poseStack, leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, color);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (hoveredSlot != null && hoveredSlot instanceof TKGhostSlot) {
            var carried = menu.getCarried();
            var placed = carried.copy().split(hoveredSlot.getMaxStackSize(carried));
            hoveredSlot.set(placed); // Temporarily update the client for continuity purposes
            TKLibNetworkHandler.sendServerboundGhostSlotClick(hoveredSlot.index);
            return true;
        }

        return super.mouseClicked(x, y, button);
    }

}

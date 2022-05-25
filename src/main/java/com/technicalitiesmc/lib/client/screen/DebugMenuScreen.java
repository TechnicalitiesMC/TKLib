package com.technicalitiesmc.lib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.menu.DebugMenu;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class DebugMenuScreen extends GenericMenuScreen<DebugMenu> {

    private int slotCount, rows;

    public DebugMenuScreen(DebugMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageHeight = 0;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.slotCount = (int) menu.slots.stream().filter(Slot::isActive).count();
        this.rows = (int) Math.ceil(slotCount / 9f);
        this.imageHeight = rows * 18 + 17 + 7;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, title, titleLabelX, titleLabelY, 0x404040);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, getTexture());
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        blit(poseStack, x, y, 0, 0, this.imageWidth, rows * 18 + 17);
        blit(poseStack, x, y + rows * 18 + 17, 0, 215, this.imageWidth, 7);
        var disabledSlots = rows * 9 - slotCount;
        if (disabledSlots > 0) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(TKLib.MODID, "textures/gui/empty.png"));
            var u = x + 7 + (9 - disabledSlots) * 18;
            var v = y + (rows - 1) * 18 + 17;
            blit(poseStack, u, v, u, v, disabledSlots * 18, 18);

        }
    }

}

package com.technicalitiesmc.lib.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.client.screen.widget.Widget;
import com.technicalitiesmc.lib.math.Vec2i;
import com.technicalitiesmc.lib.menu.TKMenu;
import com.technicalitiesmc.lib.menu.slot.ColoredSlot;
import com.technicalitiesmc.lib.menu.slot.TKGhostSlot;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TKMenuScreen<T extends TKMenu> extends AbstractContainerScreen<T> {

    private final List<Widget> widgets = new ArrayList<>();
    private final ResourceLocation texture;

    public TKMenuScreen(T menu, Inventory playerInv, Component title, ResourceLocation texture, int width, int height) {
        super(menu, playerInv, title);
        this.texture = texture;
        this.imageWidth = width;
        this.imageHeight = height;
        this.inventoryLabelY = this.imageHeight - 94;
        for (var component : menu.components()) {
            add(component.widgetSupplier().get());
        }
    }

    @Deprecated(forRemoval = true)
    public TKMenuScreen(T menu, Inventory playerInv, Component title, ResourceLocation texture) {
        this(menu, playerInv, title, texture, 176, 166);
    }

    protected final void add(Widget widget) {
        widgets.add(widget);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderWidgets(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, texture);
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

    private void renderWidgets(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, new ResourceLocation(TKLib.MODID, "textures/gui/widgets.png"));

        poseStack.pushPose();
        poseStack.translate(leftPos, topPos, 0);
        for (var widget : widgets) {
            poseStack.pushPose();
            var pos = widget.pos();
            poseStack.translate(pos.x(), pos.y(), 0);
            widget.render(poseStack, mouseX - pos.x(), mouseY - pos.y(), partialTick);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);
        var tooltip = new ArrayList<Component>();
        for (var widget : widgets) {
            Vec2i pos = widget.pos(), size = widget.size();
            int x1 = leftPos + pos.x(), y1 = topPos + pos.y();
            int x2 = x1 + size.x(), y2 = y1 + size.y();
            if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2) {
                widget.addTooltip(mouseX - x1, mouseY - y1, tooltip);
                if (!tooltip.isEmpty()) {
                    renderTooltip(poseStack, tooltip, Optional.empty(), mouseX, mouseY);
                    return;
                }
            }
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
        for (var widget : widgets) {
            Vec2i pos = widget.pos(), size = widget.size();
            int x1 = leftPos + pos.x(), y1 = topPos + pos.y();
            int x2 = x1 + size.x(), y2 = y1 + size.y();
            if (x >= x1 && x < x2 && y >= y1 && y < y2) {
                if (widget.onMouseDown(x - x1, y - y1, button)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        for (var widget : widgets) {
            Vec2i pos = widget.pos(), size = widget.size();
            int x1 = leftPos + pos.x(), y1 = topPos + pos.y();
            int x2 = x1 + size.x(), y2 = y1 + size.y();
            if (x >= x1 && x < x2 && y >= y1 && y < y2) {
                if (widget.onMouseUp(x - x1, y - y1, button)) {
                    return true;
                }
            }
        }
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amount) {
        if (hoveredSlot != null && hoveredSlot instanceof TKGhostSlot slot) {
            var stack = slot.getItem();
            if (!stack.isEmpty()) {
                var delta = (int) Math.signum(amount) * (hasShiftDown() ? 8 : 1);
                stack.setCount(Math.max(1, Math.min(stack.getCount() + delta, slot.getMaxStackSize())));
                TKLibNetworkHandler.sendServerboundGhostSlotScroll(hoveredSlot.index, delta);
            }
            return true;
        }
        for (var widget : widgets) {
            Vec2i pos = widget.pos(), size = widget.size();
            int x1 = leftPos + pos.x(), y1 = topPos + pos.y();
            int x2 = x1 + size.x(), y2 = y1 + size.y();
            if (x >= x1 && x < x2 && y >= y1 && y < y2) {
                if (widget.onMouseScrolled(x - x1, y - y1, amount)) {
                    return true;
                }
            }
        }
        return super.mouseScrolled(x, y, amount);
    }

}

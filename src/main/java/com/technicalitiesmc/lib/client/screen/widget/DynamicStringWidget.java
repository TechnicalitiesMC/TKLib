package com.technicalitiesmc.lib.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class DynamicStringWidget extends SimpleWidget {

    private final Supplier<Component> text;

    public DynamicStringWidget(int x, int y, int width, Supplier<Component> text) {
        super(x, y, width, Minecraft.getInstance().font.lineHeight);
        this.text = text;
    }

    @Override
    public void onClicked(double x, double y, int button) {
    }

    @Override
    public void addTooltip(int mouseX, int mouseY, List<Component> tooltip) {
        var font = Minecraft.getInstance().font;
        var component = text.get();
        if (font.width(component) > size().x()) {
            tooltip.add(component);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        var font = Minecraft.getInstance().font;
        RenderHelper.enableScissor(poseStack, 0, 0, size().x(), font.lineHeight);
        drawString(poseStack, font, text.get(), 0, 0, 0xFFFFFFFF);
        RenderHelper.disableScissor();
    }

}

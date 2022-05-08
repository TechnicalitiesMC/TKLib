package com.technicalitiesmc.lib.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.math.Vec2i;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface Widget {

    Vec2i pos();

    Vec2i size();

    void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks);

    default boolean onMouseDown(double x, double y, int button) {
        return false;
    }

    default boolean onMouseUp(double x, double y, int button) {
        return false;
    }

    default boolean onMouseScrolled(double x, double y, double amount) {
        return false;
    }

    default void addTooltip(int mouseX, int mouseY, List<Component> tooltip) {
    }

}

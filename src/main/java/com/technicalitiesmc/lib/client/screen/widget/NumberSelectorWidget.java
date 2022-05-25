package com.technicalitiesmc.lib.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.util.TooltipEnabled;
import com.technicalitiesmc.lib.util.value.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;

public class NumberSelectorWidget extends SimpleWidget {

    private final Reference<Integer> value;
    private final Range<Integer> range;
    @Nullable
    private final TooltipEnabled tooltipProvider;

    public NumberSelectorWidget(int x, int y, int width, int height, BooleanSupplier enabled, Reference<Integer> value,
                                Range<Integer> range, @Nullable TooltipEnabled tooltipProvider) {
        super(x, y, width, height, enabled);
        this.value = value;
        this.range = range;
        this.tooltipProvider = tooltipProvider;
    }

    @Override
    public void onClicked(double x, double y, int button) {
        if (button == 2) {
            var val = value.get();
            if (val.intValue() == range.getMinimum().intValue()) {
                value.set(range.getMaximum());
            } else {
                value.set(range.getMinimum());
            }
        }
    }

    @Override
    public boolean onMouseScrolled(double x, double y, double amount) {
        var direction = (int) Math.signum(amount);
        var scale = Screen.hasShiftDown() ? 8 : 1;
        value.set(range.fit(value.get() + direction * scale));
        return true;
    }

    @Override
    public void addTooltip(int mouseX, int mouseY, List<Component> tooltip) {
        if (tooltipProvider != null) {
            tooltipProvider.addTooltip(tooltip);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        var font = Minecraft.getInstance().font;
        var number = value.get().toString();
        var x = (size().x() - font.width(number)) / 2;
        var y = (size().y() - font.lineHeight) / 2 + 1;
        font.draw(poseStack, number, x + 1, y + 1, 0xFFAEAEAE);
        font.draw(poseStack, number, x, y, 0xFF3E3E3E);
    }

}

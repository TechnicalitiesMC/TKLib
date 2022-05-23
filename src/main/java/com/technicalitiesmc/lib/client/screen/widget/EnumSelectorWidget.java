package com.technicalitiesmc.lib.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.util.TooltipEnabled;
import com.technicalitiesmc.lib.util.value.Reference;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class EnumSelectorWidget<E extends Enum<E>> extends SimpleWidget {

    private final int u, v;
    private final Reference<E> reference;
    private final List<E> values;
    private final E defaultValue;
    @Nullable
    private final Function<E, TooltipEnabled> tooltipProvider;

    public EnumSelectorWidget(int x, int y, int width, int height, int u, int v,
                              Reference<E> reference, List<E> values, E defaultValue,
                              @Nullable Function<E, TooltipEnabled> tooltipProvider) {
        super(x, y, width, height);
        this.u = u;
        this.v = v;
        this.reference = reference;
        this.values = values;
        this.defaultValue = defaultValue;
        this.tooltipProvider = tooltipProvider;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        var index = values.indexOf(reference.get());
        blit(poseStack, 0, 0, u + index * size().y(), v, size().x(), size().y());
    }

    @Override
    public void onClicked(double x, double y, int button) {
        if (button == 0) {
            cycle(true);
        } else if (button == 1) {
            cycle(false);
        } else if (button == 2) {
            reference.set(defaultValue);
        }
    }

    @Override
    public boolean onMouseScrolled(double x, double y, double amount) {
        cycle(amount > 0);
        return true;
    }

    @Override
    public void addTooltip(int mouseX, int mouseY, List<Component> tooltip) {
        if (tooltipProvider != null) {
            var tooltipEnabled = tooltipProvider.apply(reference.get());
            if (tooltipEnabled != null) {
                tooltipEnabled.addTooltip(tooltip);
            }
        }
    }

    private void cycle(boolean forward) {
       var value = reference.get();
        if (value != null) {
            var idx = values.indexOf(value);
            var total = values.size();
            var newIdx = (idx + (forward ? 1 : total - 1)) % total;
            reference.set(values.get(newIdx));
        }
    }

}

package com.technicalitiesmc.lib.client.screen.widget;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.client.RenderHelper;
import com.technicalitiesmc.lib.util.value.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ListWidget<T extends ListWidget.Entry> extends SimpleWidget {

    private final Iterable<T> entries;
    private final Reference<Integer> selectedEntry;
    private final int entryHeight, maxVisibleEntries;
    private int scrolled;

    public ListWidget(int x, int y, int width, int height, Iterable<T> entries, Reference<Integer> selectedEntry, int entryHeight) {
        super(x, y, width, height);
        this.entries = entries;
        this.selectedEntry = selectedEntry;
        this.entryHeight = entryHeight;
        this.maxVisibleEntries = height / entryHeight;
    }

    @Override
    public void onClicked(double x, double y, int button) {
        if (button != 0) {
            return;
        }
        var viewSpaceEntry = (int) Math.floor(y / entryHeight);
        var actualEntry = viewSpaceEntry + scrolled;
        var entryCount = Iterators.size(entries.iterator());
        selectedEntry.set(actualEntry <= entryCount ? actualEntry : -1);
    }

    @Override
    public boolean onMouseScrolled(double x, double y, double amount) {
        var delta = (int) -Math.signum(amount);
        var entryCount = Iterators.size(entries.iterator());
        scrolled = Math.max(0, Math.min(scrolled + delta, entryCount - maxVisibleEntries));
        return true;
    }

    @Override
    public void addTooltip(int mouseX, int mouseY, List<Component> tooltip) {
        var viewSpaceEntry = mouseY / entryHeight;
        var actualEntry = viewSpaceEntry + scrolled;
        var entry = Iterables.getFirst(Iterables.skip(entries, actualEntry), null);
        if (entry != null) {
            entry.addTooltip(tooltip, size().x());
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        var size = size();
        int width = size.x(), height = size.y();

        RenderHelper.enableScissor(poseStack, 0, 0, width - 2, height);

        poseStack.pushPose();
        var selected = selectedEntry.get();
        var i = 0;
        for (var entry : Iterables.skip(entries, scrolled)) {
            // Draw selected background
            if (scrolled + i == selected) {
                fillGradient(poseStack, 0, 0, width, entryHeight, 0x4FFFFFFF, 0x4AFFFFFF);
            }
            // Draw entry
            entry.render(poseStack, width, mouseX, mouseY - i * entryHeight, partialTicks);
            // Continue with the next iteration
            if (++i >= maxVisibleEntries) {
                break;
            }
            poseStack.translate(0, entryHeight, 0);
        }
        poseStack.popPose();

        RenderHelper.disableScissor();
    }

    public interface Entry {

        void render(PoseStack poseStack, int width, int mouseX, int mouseY, float partialTicks);

        default void addTooltip(List<Component> components, int width) {
        }

    }

    public static final class ComponentEntry extends GuiComponent implements Entry {

        public static final int HEIGHT = 8 + 4;

        private final Component component;

        public ComponentEntry(Component component) {
            this.component = component;
        }

        @Override
        public void render(PoseStack poseStack, int width, int mouseX, int mouseY, float partialTicks) {
            var font = Minecraft.getInstance().font;
            font.drawShadow(poseStack, component, 2, 2, 0xFFFFFFFF);
        }

        @Override
        public void addTooltip(List<Component> components, int width) {
            var font = Minecraft.getInstance().font;
            if (font.width(component) > width) {
                components.add(component);
            }
        }

    }

}

package com.technicalitiesmc.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.technicalitiesmc.lib.block.TKBlock;
import com.technicalitiesmc.lib.block.TipOverlayProvider;
import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class TooltipOverlay extends GuiComponent implements IIngameOverlay {

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !(mc.hitResult instanceof BlockHitResult blockHit)) {
            return;
        }

        var state = Utils.resolveHit(mc.level, blockHit);
        var provider = TKBlock.getInterface(state.getBlock(), TipOverlayProvider.class);
        if (provider == null) {
            return;
        }

        var modifiers = new TipOverlayProvider.ModifierKeys(
                Screen.hasShiftDown(),
                Screen.hasControlDown(),
                Screen.hasAltDown()
        );
        var tooltip = provider.buildOverlay(state, mc.level, blockHit.getBlockPos(), mc.player, blockHit, modifiers);
        if (tooltip == null) {
            return;
        }

        var font = gui.getFont();
        var lineHeight = font.lineHeight + 2;
        var xOffset = width / 2 + 4;
        var yOffset = height / 2 - lineHeight * tooltip.lines().size();
        for (var line : tooltip.lines()) {
            drawString(poseStack, font, line, xOffset, yOffset, 0xFFFFFFFF);
            yOffset += lineHeight;
        }
    }

}

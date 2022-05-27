package com.technicalitiesmc.lib.client.screen.widget;

import com.technicalitiesmc.lib.math.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.function.BooleanSupplier;

public abstract class SimpleWidget extends GuiComponent implements Widget {

    private final Vec2i pos, size;
    private final BooleanSupplier enabled;
    private boolean focused;

    protected SimpleWidget(int x, int y, int width, int height, BooleanSupplier enabled) {
        this.enabled = enabled;
        this.pos = new Vec2i(x, y);
        this.size = new Vec2i(width, height);
    }

    public Vec2i pos() {
        return pos;
    }

    public Vec2i size() {
        return size;
    }

    @Override
    public boolean enabled() {
        return enabled.getAsBoolean();
    }

    protected boolean focused() {
        return focused;
    }

    public abstract void onClicked(double x, double y, int button);

    public boolean onMouseDown(double x, double y, int button) {
        focused = true;
        return true;
    }

    public boolean onMouseUp(double x, double y, int button) {
        if (focused) {
            focused = false;
            onClicked(x, y, button);
            return true;
        }
        return false;
    }

    protected final void playClickSound() {
        playClickSound(1, 1);
    }

    protected final void playClickSound(float pitch, float volume) {
        var soundManager = Minecraft.getInstance().getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch, volume * 0.25F));
    }

}

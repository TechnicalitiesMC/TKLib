package com.technicalitiesmc.lib.client.screen.widget;

import com.technicalitiesmc.lib.math.Vec2i;
import net.minecraft.client.gui.GuiComponent;

public abstract class SimpleWidget extends GuiComponent implements Widget {

    private final Vec2i pos, size;
    private boolean focused;

    protected SimpleWidget(int x, int y, int width, int height) {
        this.pos = new Vec2i(x, y);
        this.size = new Vec2i(width, height);
    }

    public Vec2i pos() {
        return pos;
    }

    public Vec2i size() {
        return size;
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

}

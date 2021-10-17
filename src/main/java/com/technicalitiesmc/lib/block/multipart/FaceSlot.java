package com.technicalitiesmc.lib.block.multipart;

import com.technicalitiesmc.lib.TKLib;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public enum FaceSlot implements BlockSlot {
    DOWN("down", Direction.DOWN),
    UP("up", Direction.UP),
    NORTH("north", Direction.NORTH),
    SOUTH("south", Direction.SOUTH),
    WEST("west", Direction.WEST),
    EAST("east", Direction.EAST);

    public static final FaceSlot[] VALUES = values();

    private final ResourceLocation registryName;
    private final Direction direction;

    FaceSlot(String name, Direction direction) {
        this.registryName = new ResourceLocation(TKLib.MODID, name);
        this.direction = direction;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Direction getDirection() {
        return direction;
    }

    public static FaceSlot of(Direction direction) {
        return VALUES[direction.ordinal()];
    }

}

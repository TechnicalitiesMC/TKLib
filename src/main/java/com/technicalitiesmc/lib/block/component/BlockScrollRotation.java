package com.technicalitiesmc.lib.block.component;

import com.mojang.math.Vector3f;
import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.ScrollingHandler;
import com.technicalitiesmc.lib.init.TKLibItemTags;
import com.technicalitiesmc.lib.init.TKLibSoundEvents;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class BlockScrollRotation extends BlockComponent.WithoutData implements ScrollingHandler {

    private static final long ROTATION_DELAY = 1;
    private static final DustParticleOptions ROTATION_PARTICLE_OPTIONS = new DustParticleOptions(
            new Vector3f(0.8F, 0.8F, 0.8F), 0.5F
    );
    private static final DustParticleOptions ROTATION_PARTICLE_OPTIONS_DARK = new DustParticleOptions(
            new Vector3f(0.2F, 0.2F, 0.2F), 0.5F
    );

    private final boolean requiresCrouching;
    private final TagKey<Item> itemTag;

    private long lastRotation = 0;

    private BlockScrollRotation(Context context, boolean requiresCrouching, TagKey<Item> itemTag) {
        super(context);
        this.requiresCrouching = requiresCrouching;
        this.itemTag = itemTag;
    }

    public static Constructor<BlockScrollRotation> of(boolean requiresCrouching, TagKey<Item> itemTag) {
        return ctx -> new BlockScrollRotation(ctx, requiresCrouching, itemTag);
    }

    public static Constructor<BlockScrollRotation> of() {
        return of(true, TKLibItemTags.TOOLS_WRENCH);
    }

    @Nullable
    @Override
    protected Object getInterface(Class<?> itf) {
        if (itf == ScrollingHandler.class) {
            return this;
        }
        return super.getInterface(itf);
    }

    @Override
    public boolean scroll(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, double scrollDelta) {
        var now = level.getGameTime();
        if (now < lastRotation + ROTATION_DELAY) {
            return true;
        }
        if ((!requiresCrouching || player.isShiftKeyDown()) && player.getMainHandItem().is(itemTag)) {
            var axis = hit.getDirection().getAxis();
            var axisPositive = hit.getDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            var rotation = (scrollDelta > 0) != axisPositive ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;

            lastRotation = now;
            TKLibNetworkHandler.sendServerboundRotateBlock(pos, axis, rotation);
            if (getBlock().rotate(state, level, pos, axis, rotation)) {
                // If the rotation was successful, play sound and spawn particles
                var d = -Math.signum((float) scrollDelta) * 0.05F;
                level.playSound(player, pos, TKLibSoundEvents.WRENCH.get(), SoundSource.PLAYERS, 0.45F, 0.95F + (float) Math.random() * 0.1F + d);

                spawnParticles(level, pos, ROTATION_PARTICLE_OPTIONS);
            } else {
                // If it wasn't successful, spawn dark particles to signify failure
                level.playSound(player, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.PLAYERS, 0.4F, 0.9F);
                level.playSound(player, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.PLAYERS, 0.4F, 1.0F);

                spawnParticles(level, pos, ROTATION_PARTICLE_OPTIONS_DARK);
            }
            return true;
        }
        return false;
    }

    private void spawnParticles(Level level, BlockPos pos, DustParticleOptions options) {
        var res = 6;
        for (int x = 0; x <= res; x++) {
            var xFace = x == 0 || x == res;
            for (int y = 0; y <= res; y++) {
                var yFace = y == 0 || y == res;
                for (int z = 0; z <= res; z++) {
                    var zFace = z == 0 || z == res;
                    if ((xFace ? 1 : 0) + (yFace ? 1 : 0) + (zFace ? 1 : 0) >= 2) {
                        level.addParticle(
                                options,
                                pos.getX() + x / (float) res,
                                pos.getY() + y / (float) res,
                                pos.getZ() + z / (float) res,
                                0, 0, 0
                        );
                    }
                }
            }
        }
    }

}

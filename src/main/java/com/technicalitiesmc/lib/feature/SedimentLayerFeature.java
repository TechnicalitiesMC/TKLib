package com.technicalitiesmc.lib.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class SedimentLayerFeature extends Feature<SedimentLayerFeature.Configuration> {

    public SedimentLayerFeature(Codec<Configuration> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        var config = context.config();
        var origin = context.origin();
        var level = context.level();
        var random = context.random();

        var state = config.state();
        var target = config.target();
        var thickness = config.thickness().sample(random);
        var tilt = Math.toRadians(config.tiltDegrees().sample(random));
        var tiltDirection = Math.toRadians(360 * random.nextFloat());
        var radiusX = config.radius().sample(random);
        var radiusZ = config.radius().sample(random);

        if (thickness <= 0 || radiusX <= 0 || radiusZ <= 0) {
            return false;
        }

        var slope = Math.tan(tilt);
        var slopeX = Math.cos(tiltDirection) * slope;
        var slopeZ = Math.sin(tiltDirection) * slope;

        var generated = false;
        for (var x = -radiusX; x < radiusX; x++) {
            var dx = x / (float) radiusX;
            for (var z = -radiusZ; z < radiusZ; z++) {
                if (!isInSquircle(x, z, radiusX, radiusZ, 4)) {
                    continue;
                }

                var dz = z / (float) radiusZ;
                var y = x * slopeX + z * slopeZ;

                var linearDistanceToCenter = 1 - Math.max(Math.abs(dx), Math.abs(dz));
                var localThickness = (int) Math.ceil(thickness * Math.sqrt(linearDistanceToCenter));

                for (var yOff = 0; yOff < localThickness; yOff++) {
                    var pos = origin.offset(x, y + yOff, z);
                    var currentState = level.getBlockState(pos);
                    if (target.test(currentState, random)) {
                        generated = true;
                        level.setBlock(pos, state, 2);
                    }
                }
            }
        }
        return generated;
    }

    private static boolean isInSquircle(float x, float y, float width, float height, float pinchFactor) {
        var sdf = Math.pow(x / width, pinchFactor) + Math.pow(y / height, pinchFactor);
        return sdf <= 1;
    }

    public record Configuration(
            BlockState state,
            IntProvider thickness,
            IntProvider radius,
            FloatProvider tiltDegrees,
            RuleTest target
    ) implements FeatureConfiguration {

        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(
                    BlockState.CODEC.fieldOf("state").forGetter(Configuration::state),
                    IntProvider.codec(1, 32).fieldOf("thickness").forGetter(Configuration::thickness),
                    IntProvider.codec(1, 32).fieldOf("radius").forGetter(Configuration::radius),
                    FloatProvider.codec(0, 90).fieldOf("tilt_degrees").forGetter(Configuration::tiltDegrees),
                    RuleTest.CODEC.fieldOf("target").forGetter(Configuration::target)
            ).apply(builder, Configuration::new);
        });

    }

}

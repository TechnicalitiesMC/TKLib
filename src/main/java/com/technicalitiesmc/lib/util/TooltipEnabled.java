package com.technicalitiesmc.lib.util;

import com.technicalitiesmc.lib.TKLib;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.List;

public interface TooltipEnabled {

    void addTooltip(List<Component> tooltip);

    interface Auto extends TooltipEnabled {

        String getTooltipTranslationKey();

        @Override
        default void addTooltip(List<Component> tooltip) {
            var key = getTooltipTranslationKey();
            tooltip.addAll(TextUtils.getLocalized(key));
            var subtitle = new TranslatableComponent(key + ".subtitle");
            if (!subtitle.getKey().equals(subtitle.getContents())) {
                tooltip.addAll(TextUtils.breakUp(subtitle));
            }
        }

    }

    class BuiltIn {

        private static final EnumMap<DyeColor, Component> COMPONENTS = Utils.newFilledEnumMap(DyeColor.class,
                color -> new TranslatableComponent("tooltip." + TKLib.MODID + ".color." + color.getSerializedName())
        );

        public static TooltipEnabled of(DyeColor dye) {
            var component = nameOf(dye);
            return list -> list.add(component);
        }

        public static Component nameOf(DyeColor dye) {
            return COMPONENTS.get(dye);
        }

    }

}

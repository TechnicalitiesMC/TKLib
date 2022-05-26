package com.technicalitiesmc.lib.util;

import com.technicalitiesmc.lib.TKLib;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;

import java.util.EnumMap;
import java.util.List;

public interface TooltipProvider {

    void addTooltip(List<Component> tooltip);

    interface Auto extends TooltipProvider {

        String getTooltipTranslationKey();

        @Override
        default void addTooltip(List<Component> tooltip) {
            var key = getTooltipTranslationKey();
            tooltip.addAll(TextUtils.getLocalized(key));
            var subtitleKey = key + ".subtitle";
            var subtitle = new TranslatableComponent(subtitleKey);
            var subtitleString = subtitle.getString();
            if (!subtitleString.equals(subtitleKey)) {
                tooltip.addAll(TextUtils.breakUp(subtitleString));
            }
        }

    }

    record Simple(String translationKey) implements TooltipProvider.Auto {

        @Override
        public String getTooltipTranslationKey() {
            return translationKey();
        }

    }

    class BuiltIn {

        private static final EnumMap<DyeColor, Component> COMPONENTS = Utils.newFilledEnumMap(DyeColor.class,
                color -> new TranslatableComponent("tooltip." + TKLib.MODID + ".color." + color.getSerializedName())
        );

        public static TooltipProvider of(DyeColor dye) {
            var component = nameOf(dye);
            return list -> list.add(component);
        }

        public static Component nameOf(DyeColor dye) {
            return COMPONENTS.get(dye);
        }

    }

}

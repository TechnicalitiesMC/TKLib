package com.technicalitiesmc.lib.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {

    public static List<Component> getLocalized(String key, Object... args) {
        return breakUp(new TranslatableComponent(key, args));
    }

    public static List<Component> breakUp(Component component) {
        return Arrays.stream(component.getContents().split("\n"))
            .map(TextComponent::new)
            .collect(Collectors.toList());
    }

}
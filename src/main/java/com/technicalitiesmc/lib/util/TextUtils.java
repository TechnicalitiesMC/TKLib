package com.technicalitiesmc.lib.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextUtils {

    public static List<Component> getLocalized(String key, Object... args) {
        var component = new TranslatableComponent(key, args);
        var contents = component.getString();
        if (contents.equals(key)) {
            return List.of(component);
        }
        return breakUp(component);
    }

    public static List<Component> breakUp(Component component) {
        return breakUp(component.getString());
    }

    public static List<Component> breakUp(String contents) {
        return Arrays.stream(contents.split("\n"))
            .map(TextComponent::new)
            .collect(Collectors.toList());
    }

}
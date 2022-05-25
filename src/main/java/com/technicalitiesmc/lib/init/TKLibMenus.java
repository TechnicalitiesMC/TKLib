package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.menu.DebugMenu;
import com.technicalitiesmc.lib.menu.LockedChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TKLibMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, TKLib.MODID);

    public static final RegistryObject<MenuType<LockedChestMenu>> LOCKED_GENERIC_9x3 = register("locked_chest_9x3", LockedChestMenu::threeRows);
    public static final RegistryObject<MenuType<LockedChestMenu>> LOCKED_GENERIC_9x6 = register("locked_chest_9x6", LockedChestMenu::sixRows);
    public static final RegistryObject<MenuType<DebugMenu>> DEBUG = register("debug", DebugMenu::new);

    // Helpers
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> factory) {
        return REGISTRY.register(name, () -> new MenuType<>(factory));
    }

}

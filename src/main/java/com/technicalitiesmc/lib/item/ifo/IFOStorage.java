package com.technicalitiesmc.lib.item.ifo;

import javax.annotation.Nullable;

public class IFOStorage {

    private @Nullable ItemFunctionalOverride activeOverride;

    @Nullable
    public ItemFunctionalOverride getActiveOverride() {
        return activeOverride;
    }

    public void setActiveOverride(ItemFunctionalOverride activeOverride) {
        this.activeOverride = activeOverride;
    }

}

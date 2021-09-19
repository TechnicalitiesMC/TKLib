package com.technicalitiesmc.lib.util;

public enum TickingSide {
    SERVER(true, false),
    CLIENT(false, true),
    NEITHER(false, false),
    BOTH(true, true);

    private boolean server, client;

    TickingSide(boolean server, boolean client) {
        this.server = server;
        this.client = client;
    }

    public boolean isServer() {
        return server;
    }

    public boolean isClient() {
        return client;
    }

}

package me.nixuge.serverlistbufferfixer.core;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;

import java.net.UnknownHostException;

public class Run2 extends Thread {

    private final GuiMultiplayer owner;

    private final ServerData server;

    public Run2(GuiMultiplayer g, ServerData s) {
        owner = g;
        server = s;
    }

    @Override
    public void run() {
        try {
            owner.getOldServerPinger().ping(server);
        } catch (UnknownHostException e) {
            LogManager.getLogger().error(e);
        } catch (NullPointerException ignored) {}
    }
}

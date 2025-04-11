package me.nixuge.serverlistbufferfixer.core;

import net.minecraft.client.multiplayer.ServerData;

public interface IOldServerPinger {

    void call(ServerData server);
}

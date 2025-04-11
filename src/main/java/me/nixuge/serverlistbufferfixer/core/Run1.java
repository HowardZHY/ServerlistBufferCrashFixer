package me.nixuge.serverlistbufferfixer.core;

import me.nixuge.serverlistbufferfixer.McMod;
import me.nixuge.serverlistbufferfixer.config.ConfigCache;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Run1 implements Runnable {

    private final ServerData server;

    private final Future<?> future;

    public Run1(ServerData server, Future<?> future) {
        this.server = server;
        this.future = future;
    }

    public void run() {
        try {
            future.get(config.getServerTimeout(), TimeUnit.SECONDS);
        } catch (TimeoutException e1) {
            setServerFail(server, EnumChatFormatting.RED + "Timed out");
        } catch (ExecutionException e2) {
            if (e2.getCause() instanceof UnknownHostException) {
                setServerFail(server, EnumChatFormatting.DARK_RED + "Can't resolve hostname");
            } else {
                setServerFail(server, EnumChatFormatting.DARK_RED + "Can't connect to server.");
            }
        } catch (Exception e3) {
            // Shouldn't happen anymore but just in case
            setServerFail(server, EnumChatFormatting.DARK_RED + "Can't connect to server.");
        }
        runningTaskCount--;
    }

    public static final ConfigCache config = McMod.getInstance().getConfigCache();

    public static int runningTaskCount = 0;

    public static void setServerFail(ServerData server, String error) {
        server.pingToServer = -1L;
        server.serverMOTD = error;
    }
}

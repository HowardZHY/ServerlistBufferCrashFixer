package me.nixuge.serverlistbufferfixer.mixins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.nixuge.serverlistbufferfixer.McMod;
import me.nixuge.serverlistbufferfixer.core.Run1;
import me.nixuge.serverlistbufferfixer.core.Run2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.*;

@Mixin(ServerListEntryNormal.class)
public abstract class ServerListEntryNormalMixin {

    @Shadow
    @Final
    private GuiMultiplayer owner;

    @Shadow
    @Final
    private ServerData server;

    @Shadow
    @Final
    @Mutable
    private static ThreadPoolExecutor field_148302_b;

    // still callign mcMod.getinstance.getconfigcache just in case
    private static final int MAX_THREAD_COUNT_PINGER = McMod.getInstance().getConfigCache().getMaxThreadCountPinger();

    private static final int MAX_THREAD_COUNT_TIMEOUT = McMod.getInstance().getConfigCache().getMaxThreadCountTimeout();

    // Note: if servers are added, this will be inaccurate
    // But it should be good enough still
    // Can't bother to mixin onto some other classes just to change that (rn at least).
    private static final int serverCountCache;

    static {
        serverCountCache = new ServerList(Minecraft.getMinecraft()).countServers();
        // Note: not even sure this reassignement works since the field is final
        field_148302_b = new ScheduledThreadPoolExecutor(Math.min(serverCountCache + 5, MAX_THREAD_COUNT_PINGER), (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
    }

    private final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(Math.min(serverCountCache + 5, MAX_THREAD_COUNT_TIMEOUT));

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"))
    public Future<?> drawEntry(ThreadPoolExecutor instance, Runnable r) {
        // Check if too many running tasks, if yes cancel & set to "spamming"
        if (Run1.runningTaskCount > serverCountCache * 2) {
            server.pingToServer = -1L;
            server.serverMOTD = EnumChatFormatting.GRAY + "Spamming...";
            return field_148302_b.submit(() -> {});
        }

        // Start up the timeout task
        final Future<?> future = timeoutExecutor.submit(new Run2(owner, server));
        Run1.runningTaskCount++;

        // "Vanilla" behavior, modified to:
        // - use a timeout for the task instead of the ping directly
        // - handle future.get()'s exceptions instead of the ping's exceptions
        return field_148302_b.submit(new Run1(server, future));
    }
}

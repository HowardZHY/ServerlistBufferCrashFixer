package me.nixuge.serverlistbufferfixer.mixins;

import me.nixuge.serverlistbufferfixer.core.IOldServerPinger;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OldServerPinger.class)
public abstract class OldServerPingerMixin implements IOldServerPinger {

    @Shadow
    private static @Final Logger logger;

    @Shadow
    private void tryCompatibilityPing(final ServerData server) {}

    @Inject(method = "access$100", at = @At("HEAD"), cancellable = true, remap = false)
    private static void inject$access(OldServerPinger instance, ServerData server, CallbackInfo ci) {
        try {
            IOldServerPinger pinger = (IOldServerPinger) instance;
            pinger.call(server);
        } catch (Exception e) {
            logger.error(e);
        }
        ci.cancel();
    }

    @Override
    public void call(ServerData server) {
        this.tryCompatibilityPing(server);
    }
}

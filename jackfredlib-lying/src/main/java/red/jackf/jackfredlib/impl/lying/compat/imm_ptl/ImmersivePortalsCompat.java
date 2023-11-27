package red.jackf.jackfredlib.impl.lying.compat.imm_ptl;

import net.minecraft.server.level.ServerLevel;
import qouteall.imm_ptl.core.network.PacketRedirection;

public class ImmersivePortalsCompat {
    public static void runWrapped(ServerLevel level, Runnable call) {
        PacketRedirection.withForceRedirect(level, call);
    }
}

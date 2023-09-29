package red.jackf.jackfredlib.client.mixins.gps;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(PlayerTabOverlay.class)
public interface PlayerTabOverlayAccessor {

    /**
     * Easier to just copy what vanilla does in case it changes
     */
    @Invoker("getPlayerInfos")
    List<PlayerInfo> jflib$getPlayerInfos();
}

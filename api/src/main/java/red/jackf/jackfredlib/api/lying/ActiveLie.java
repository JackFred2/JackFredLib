package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

public record ActiveLie<L extends Lie>(ServerPlayer player, L lie) {
    public void fade() {
        LiesImpl.LOGGER.debug("Fading lie for {}", player.getName());
        this.lie.fade(player);
        LiesImpl.INSTANCE.remove(this);
    }
}

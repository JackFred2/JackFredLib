package red.jackf.jackfredlib.impl.lying.glowing;

import net.minecraft.server.level.ServerPlayer;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

public class ActiveEntityGlowLie extends ActiveLie<EntityGlowLie> {
    /**
     * Create a new active lie instance. Should not be used directly; use a method in {@link Lies}
     *
     * @param player Player to send the lie to
     * @param lie    Lie to send to said player
     */
    public ActiveEntityGlowLie(ServerPlayer player, EntityGlowLie lie) {
        super(player, lie);
    }

    @Override
    protected void removeFromLie() {
        LiesImpl.INSTANCE.removeEntityGlow(this);
    }
}

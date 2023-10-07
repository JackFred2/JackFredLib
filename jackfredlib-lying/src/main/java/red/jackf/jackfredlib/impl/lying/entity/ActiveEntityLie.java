package red.jackf.jackfredlib.impl.lying.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

public class ActiveEntityLie<E extends Entity> extends ActiveLie<EntityLie<E>> {
    /**
     * Create a new active entity lie instance. Should not be used directly; use a method in {@link Lies}
     *
     * @param player Player to send the lie to
     * @param lie    entity Lie to send to said player
     */
    public ActiveEntityLie(ServerPlayer player, EntityLie<E> lie) {
        super(player, lie);
    }

    @Override
    protected void removeFromLie() {
        LiesImpl.INSTANCE.removeEntity(this);
    }
}

package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerPlayer;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

public interface Lies {
    Lies INSTANCE = LiesImpl.INSTANCE;

    ActiveLie<EntityLie> addEntity(ServerPlayer player, EntityLie entityLie);
}

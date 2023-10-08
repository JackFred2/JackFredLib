package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.base.LogUtil;
import red.jackf.jackfredlib.impl.lying.entity.ActiveEntityLie;
import red.jackf.jackfredlib.impl.lying.glowing.ActiveEntityGlowLie;

import java.util.Optional;

public class LiesImpl implements Lies {
    public static final Logger LOGGER = LogUtil.getLogger("Lies");
    public static final LiesImpl INSTANCE = new LiesImpl();

    private final Multimap<GameProfile, ActiveEntityLie<? extends Entity>> activeEntityLies = ArrayListMultimap.create();

    private final Multimap<GameProfile, ActiveEntityGlowLie> activeEntityGlowLies = ArrayListMultimap.create();

    @Override
    public <E extends Entity> ActiveLie<EntityLie<E>> addEntity(ServerPlayer player, EntityLie<E> entityLie) {
        var active = new ActiveEntityLie<>(player, entityLie);
        activeEntityLies.put(player.getGameProfile(), active);
/*
        var entity = entityLie.entity();
        var packets = new ArrayList<Packet<ClientGamePacketListener>>();
        packets.add(entity.getAddEntityPacket());
        var data = entity.getEntityData().packDirty();
        if (data != null)
            packets.add(new ClientboundSetEntityDataPacket(entity.getId(), data));
        if (entity instanceof LivingEntity living) {
            var attributes = living.getAttributes().getSyncableAttributes();
            if (!attributes.isEmpty())
                packets.add(new ClientboundUpdateAttributesPacket(entity.getId(), attributes));

            var equipment = new ArrayList<Pair<EquipmentSlot, ItemStack>>();
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = living.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    equipment.add(Pair.of(slot, stack.copy()));
                }
            }
            if (!equipment.isEmpty())
                packets.add(new ClientboundSetEquipmentPacket(entity.getId(), equipment));
        }

        player.connection.send(new ClientboundBundlePacket(packets));*/

        active.lie().setup(player);

        return active;
    }

    @Override
    public ActiveLie<EntityGlowLie> addEntityGlow(ServerPlayer player, EntityGlowLie entityGlowLie) {
        var active = new ActiveEntityGlowLie(player, entityGlowLie);
        activeEntityGlowLies.put(player.getGameProfile(), active);

        active.lie().setup(player);

        return active;
    }

    public Optional<ActiveEntityLie<? extends Entity>> getEntityLieFromId(ServerPlayer player, int entityId) {
        return activeEntityLies.get(player.getGameProfile())
                .stream()
                .filter(active -> active.lie().entity().getId() == entityId)
                .findFirst();
    }

    public Optional<ActiveEntityGlowLie> getEntityGlowLieFromId(ServerPlayer player, int entityId) {
        return activeEntityGlowLies.get(player.getGameProfile())
                .stream()
                .filter(active -> active.lie().entity().getId() == entityId)
                .findFirst();
    }

    public void removeEntity(ActiveEntityLie<?> activeLie) {
        activeEntityLies.get(activeLie.player().getGameProfile()).remove(activeLie);
    }

    public void removeEntityGlow(ActiveEntityGlowLie activeLie) {
        activeEntityGlowLies.get(activeLie.player().getGameProfile()).remove(activeLie);
    }

    public void tick() {
        activeEntityLies.values().forEach(GenericsUtils::tickEntityLie);
    }
}

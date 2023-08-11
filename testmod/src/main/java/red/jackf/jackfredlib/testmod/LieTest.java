package red.jackf.jackfredlib.testmod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Items;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;

public class LieTest {
    private static Slime makeSlime(ServerLevel serverLevel, BlockPos pos) {
        var entity = EntityType.SLIME.create(
                serverLevel,
                null,
                null,
                pos.above(),
                MobSpawnType.COMMAND,
                false,
                false
        );
        if (entity != null) {
            entity.setSize(1, false);
            entity.setXRot(0f);
            entity.setYRot(0f);
            entity.setOldPosAndRot();
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(entity.getYRot());
            entity.setNoGravity(true);
            entity.setNoAi(true);
            var x = pos.getX() + 0.5;
            var y = pos.getY() + 0.5 - entity.getBbHeight() / 2;
            var z = pos.getZ() + 0.5;
            entity.setPos(x, y, z);
            entity.setGlowingTag(true);
            entity.setInvisible(false);
        }
        return entity;
    }

    public static void setup() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (handStack.is(Items.DIAMOND_AXE)) {
                    var lie = Lies.INSTANCE.addEntity(serverPlayer, EntityLie.builder(makeSlime(serverLevel, hitResult.getBlockPos().offset(hitResult.getDirection()
                            .getNormal())))
                            .onLeftClick((activeLie, shiftDown, pos) -> {
                                activeLie.player().sendSystemMessage(Component.literal("left clicked " + pos));
                                if (shiftDown) activeLie.fade();
                            }).onRightClick(((activeLie, shiftDown, hand1, pos) -> {
                                if (hand1 == InteractionHand.MAIN_HAND)
                                    activeLie.player().sendSystemMessage(Component.literal("right clicked" + pos));
                            }))
                            .build());
                    Debris.INSTANCE.schedule(lie, 10 * SharedConstants.TICKS_PER_SECOND);
                }
            }
            return InteractionResult.PASS;
        });
    }

}

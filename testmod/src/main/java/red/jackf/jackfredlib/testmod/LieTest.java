package red.jackf.jackfredlib.testmod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.api.lying.entity.EntityUtil;

public class LieTest {
    public static void setup() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (handStack.is(Items.DIAMOND_AXE)) {
                    var lie = Lies.INSTANCE.addEntity(serverPlayer, EntityLie.builder(EntityUtil.glowingCube(serverLevel, hitResult.getBlockPos().offset(hitResult.getDirection()
                            .getNormal())))
                            .onLeftClick((activeLie, shiftDown, pos) -> {
                                activeLie.player().sendSystemMessage(Component.literal("left clicked " + pos));
                                if (shiftDown) activeLie.fade();
                            }).onRightClick(((activeLie, shiftDown, hand1, pos) -> {
                                if (hand1 == InteractionHand.MAIN_HAND)
                                    activeLie.player().sendSystemMessage(Component.literal("right clicked" + pos));
                            })).onTick(activeLie -> {
                                var entity = activeLie.lie().entity();
                                entity.setPos(entity.position().add(new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).scale(0.1)));
                            })
                            .build());
                    Debris.INSTANCE.schedule(lie, 20 * SharedConstants.TICKS_PER_SECOND);
                }
            }
            return InteractionResult.PASS;
        });
    }

}

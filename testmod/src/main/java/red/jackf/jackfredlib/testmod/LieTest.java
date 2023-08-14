package red.jackf.jackfredlib.testmod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.api.lying.entity.EntityPresets;

public class LieTest {
    public static void setup() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (handStack.is(Items.DIAMOND_AXE)) {
                    var lie = Lies.INSTANCE.addEntity(serverPlayer,
                            EntityLie.builder(EntityPresets.highlight(serverLevel, hitResult.getBlockPos(), Colour.fromInt(0xFFBADA55), 0.5f))
                                    .onLeftClick((activeLie, shiftDown, relativeToEntity) -> activeLie.fade())
                                    .onRightClick((activeLie, shiftDown, hand1, relativeToEntity) -> activeLie.fade())
                                    .onFade(activeLie -> activeLie.player().sendSystemMessage(Component.literal("faded")))
                                    .build());
                    Debris.INSTANCE.schedule(lie, 20 * SharedConstants.TICKS_PER_SECOND);
                }
            }
            return InteractionResult.PASS;
        });
    }

}

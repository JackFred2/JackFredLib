package red.jackf.jackfredlib.testmod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector3f;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.Lies;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.api.lying.entity.EntityPresets;
import red.jackf.jackfredlib.api.lying.entity.EntityUtils;
import red.jackf.jackfredlib.api.lying.entity.builders.EntityBuilders;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.LiesImpl;

import java.util.HashMap;
import java.util.Map;

public class LieTest {
    public static void setup() {
        setupHooks();

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (ENTITY_LIES.containsKey(handStack.getItem())) {
                    var entity = ENTITY_LIES.get(handStack.getItem()).apply(serverLevel, hitResult.getBlockPos().offset(hitResult.getDirection().getNormal()), serverPlayer);
                    var lie = Lies.INSTANCE.addEntity(serverPlayer, entity);
                    Debris.INSTANCE.schedule(lie, 20 * SharedConstants.TICKS_PER_SECOND);
                }
            }
            return InteractionResult.PASS;
        });
    }

    private static final Map<Item, TriFunction<ServerLevel, BlockPos, ServerPlayer, EntityLie<?>>> ENTITY_LIES = new HashMap<>();

    private static void setupHooks() {
        ENTITY_LIES.put(Items.DIAMOND_AXE, (level, pos, player) -> {
            var entity = EntityPresets.highlight(level, pos, Colour.fromHSV((float) Math.random(), 1f, 1f), (float) Math.random());
            return EntityLie.builder(entity)
                    .onLeftClick((activeLie, shiftDown, relativeToEntity) -> activeLie.fade())
                    .onRightClick((activeLie, shiftDown, hand1, relativeToEntity) -> activeLie.fade())
                    .onFade(activeLie -> activeLie.player().sendSystemMessage(Component.literal("faded")))
                    .build();
        });
        ENTITY_LIES.put(Items.GOLDEN_AXE, (level, pos, player) -> {
            var entity = EntityBuilders.itemDisplay(level)
                    .stack(new ItemStack(Items.DIAMOND_PICKAXE))
                    .displayContext(ItemDisplayContext.GROUND)
                    .positionCentered(pos)
                    .scale(new Vector3f(1.5f, 1.5f, 1.5f))
                    .build();
            return EntityLie.builder(entity)
                    .onTick(active -> active.lie().entity().setYRot(active.lie().entity().getYRot() + 3f))
                    .build();
        });
        ENTITY_LIES.put(Items.IRON_AXE, (level, pos, player) -> {
           var text = EntityBuilders.textDisplay(level)
                   .text(Component.literal(" I W ".repeat(26)).withStyle(ChatFormatting.AQUA))
                   .lineWidth(100)
                   .backgroundColour(64, 0, 127, 0)
                   .seeThrough(true)
                   .textOpacity(200)
                   .hasShadow(true)
                   .billboard(Display.BillboardConstraints.HORIZONTAL)
                   .position(pos)
                   .facing(player)
                   .textAlign(Display.TextDisplay.Align.LEFT)
                   .build();
           return EntityLie.builder(text).build();
        });
        ENTITY_LIES.put(Items.DIAMOND_HOE, (level, pos, player) -> {
            var entity = EntityBuilders.generic(EntityType.ALLAY, level)
                    .position(pos)
                    .facing(player)
                    .customName(Component.literal("Coloured Glow non-display"))
                    .alwaysRenderName(true)
                    .build();
            return EntityLie.builder(entity)
                    .onRightClick((activeLie, shiftDown, hand, relativeToEntity) -> EntityUtils.face(
                            activeLie.lie().entity(), activeLie.player()))
                    .build();
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand == InteractionHand.MAIN_HAND && hitResult != null && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (handStack.is(Items.GLOW_INK_SAC)) {
                    var existing = LiesImpl.INSTANCE.getEntityGlowLieFromId(serverPlayer, entity.getId());
                    if (existing.isPresent()) {
                        existing.get().fade();
                    } else {
                        var lie = Lies.INSTANCE.addEntityGlow(serverPlayer, EntityGlowLie.builder(entity)
                                .colour(ChatFormatting.getById((int) (Math.random() * 16)))
                                .build());
                        Debris.INSTANCE.schedule(lie, 30 * SharedConstants.TICKS_PER_SECOND);
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}

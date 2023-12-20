package red.jackf.jackfredlib.testmod;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.function.TriFunction;
import org.joml.Vector3f;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.Tracker;
import red.jackf.jackfredlib.api.lying.entity.EntityLie;
import red.jackf.jackfredlib.api.lying.entity.EntityUtils;
import red.jackf.jackfredlib.api.lying.entity.builders.EntityBuilders;
import red.jackf.jackfredlib.api.lying.entity.builders.EntityPresets;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.LieManager;

import java.util.HashMap;
import java.util.Map;

public class LieTest {
    public static void setup() {
        setupHooks();

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (ENTITY_LIES.containsKey(handStack.getItem())) {
                    var entityLie = ENTITY_LIES.get(handStack.getItem()).apply(serverLevel, hitResult.getBlockPos().offset(hitResult.getDirection().getNormal()), serverPlayer);
                    if (handStack.getItem() != Items.NETHERITE_HOE)
                        Debris.INSTANCE.schedule(entityLie, 20 * SharedConstants.TICKS_PER_SECOND);
                }
            }
            return InteractionResult.PASS;
        });
    }

    private static final Map<Item, TriFunction<ServerLevel, BlockPos, ServerPlayer, EntityLie<?>>> ENTITY_LIES = new HashMap<>();

    private static void setupHooks() {
        ENTITY_LIES.put(Items.DIAMOND_AXE, (level, pos, player) -> {
            Entity entity = EntityPresets.highlight(level, pos, Colour.fromHSV((float) Math.random(), 1f, 1f), (float) Math.random());
            return EntityLie.builder(entity)
                    .onLeftClick((player1, lie, wasSneaking, relativeToEntity) -> lie.removePlayer(player1))
                    .onRightClick((player1, lie, wasSneaking, hand, relativeToEntity) -> lie.removePlayer(player1))
                    .onFade((player1, entityLie) -> player1.sendSystemMessage(Component.literal("faded")))
                    .createAndShow(player);
        });
        ENTITY_LIES.put(Items.GOLDEN_AXE, (level, pos, player) -> {
            var entity = EntityBuilders.itemDisplay(level)
                    .stack(new ItemStack(Items.DIAMOND_PICKAXE))
                    .displayContext(ItemDisplayContext.GROUND)
                    .positionCentered(pos)
                    .scale(new Vector3f(1.5f, 1.5f, 1.5f))
                    .build();
            return EntityLie.builder(entity)
                    .onTick((serverPlayer, entityLie) -> entityLie.entity().setYRot(entityLie.entity().getYRot() + 3f))
                    .createAndShow(player);
        });
        ENTITY_LIES.put(Items.IRON_AXE, (level, pos, player) -> {
           var text = EntityBuilders.textDisplay(level)
                   .text(Component.literal(" I W ".repeat((int) (Math.random() * 32))).withStyle(ChatFormatting.AQUA))
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
           return EntityLie.builder(text).createAndShow(player);
        });
        ENTITY_LIES.put(Items.DIAMOND_HOE, (level, pos, player) -> {
            var entity = EntityBuilders.generic(EntityType.ALLAY, level)
                    .position(pos)
                    .facing(player)
                    .customName(Component.literal("Coloured Glow non-display"))
                    .alwaysRenderName(true)
                    .glowing(true)
                    .build();
            EntityLie.TickCallback<Allay> tickCallback = player.isShiftKeyDown() ? (player1, lie) -> lie.setGlowColour(randomColour()) : null;
            return EntityLie.builder(entity)
                    .onRightClick((player1, lie, wasSneaking, hand, relativeToEntity) -> EntityUtils.face(lie.entity(), player1))
                    .onTick(tickCallback)
                    .glowColour(ChatFormatting.AQUA)
                    .createAndShow(player);
        });
        ENTITY_LIES.put(Items.GOLDEN_HOE, (level, pos, player) -> {
            var entity = EntityBuilders.generic(EntityType.ALLAY, level)
                    .position(pos)
                    .facing(player)
                    .alwaysRenderName(true)
                    .build();
            return EntityLie.builder(entity)
                    .onTick((player1, lie) -> {
                        if (Math.random() < 0.02) lie.fade();
                    })
                    .glowColour(ChatFormatting.RED)
                    .createAndShow(player);
        });
        ENTITY_LIES.put(Items.NETHERITE_HOE, ((level, blockPos, player) -> {
            var entity = EntityBuilders.generic(EntityType.SLIME, level)
                    .position(blockPos)
                    .facing(player)
                    .customName(Component.literal("Tracker test"))
                    .glowing(true)
                    .build();
            var lie1 = EntityLie.builder(entity)
                                .onRightClick(((player1, lie, wasSneaking, hand, relativeToEntity) -> {
                        lie.fade();
                    })).glowColour(ChatFormatting.GREEN).createAndShow();

            Tracker.<EntityLie<Slime>>builder(level)
                    .addLie(lie1)
                    .setFocus(blockPos.getCenter(), 8f)
                    .addPredicate(ServerPlayer::isShiftKeyDown)
                    .setUpdateInterval(60)
                    .build(true);

            return lie1;
        }));

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand == InteractionHand.MAIN_HAND && hitResult != null && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (handStack.is(Items.GLOW_INK_SAC)) {
                    var existing = LieManager.INSTANCE.getEntityGlowLieFromEntityUuid(serverPlayer, entity.getUUID());
                    if (existing.isPresent()) {
                        existing.get().fade();
                    } else {
                        System.out.println("ID: " + entity.getId());
                        var lie = EntityGlowLie.builder(entity)
                                .colour(randomColour())
                                .onFade((player2, lie2) -> player2.playSound(SoundEvents.NOTE_BLOCK_CHIME.value()))
                                .onTick(player.isShiftKeyDown() ? null : (player2, lie2) -> {
                                    if (lie2.entity().getLevel().getGameTime() % 20 == 0) {
                                        var existingCol = lie2.glowColour();
                                        if (existingCol != null) {
                                            lie2.setGlowColour(null);
                                        } else {
                                            lie2.setGlowColour(randomColour());
                                        }
                                    }
                                })
                                .createAndShow(serverPlayer);
                        Debris.INSTANCE.schedule(lie, 30 * SharedConstants.TICKS_PER_SECOND);
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    private static ChatFormatting randomColour() {
        return ChatFormatting.getById((int) (Math.random() * 16));
    }
}

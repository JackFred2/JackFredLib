package red.jackf.jackfredlib.testmod;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.glowing.EntityGlowLie;
import red.jackf.jackfredlib.impl.lying.LieManager;

public class LieTest {
    public static void setup() {
        setupHooks();
/*
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
        });*/
    }

    // private static final Map<Item, TriFunction<ServerLevel, BlockPos, ServerPlayer, EntityLie<?>>> ENTITY_LIES = new HashMap<>();

    private static void setupHooks() {
        /*
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
                    .glowing(true)
                    .build();
            return EntityLie.builder(entity)
                    .onRightClick((activeLie, shiftDown, hand, relativeToEntity) -> EntityUtils.face(
                            activeLie.lie().entity(), activeLie.player()))
                    .glowColour(ChatFormatting.AQUA)
                    .build();
        });*/

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand == InteractionHand.MAIN_HAND && hitResult != null && player instanceof ServerPlayer serverPlayer) {
                var handStack = player.getItemInHand(hand);
                if (handStack.is(Items.GLOW_INK_SAC)) {
                    var existing = LieManager.INSTANCE.getEntityGlowLieFromEntityId(serverPlayer, entity.getId());
                    if (existing.isPresent()) {
                        existing.get().fade();
                    } else {
                        var lie = EntityGlowLie.builder(entity)
                                .colour(ChatFormatting.getById((int) (Math.random() * 16)))
                                .onFade((player2, lie2) -> {
                                    player2.playSound(SoundEvents.NOTE_BLOCK_CHIME.value());
                                }).onTick((player2, lie2) -> {
                                    if (lie2.entity().level().getGameTime() % 15 == 0) {
                                        lie2.setGlowColour(ChatFormatting.getById((int) (Math.random() * 16)));
                                    }
                                })
                                .createAndShow(serverPlayer);
                        Debris.INSTANCE.schedule(lie, 15 * SharedConstants.TICKS_PER_SECOND);
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}

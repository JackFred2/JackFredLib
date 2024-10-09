package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import red.jackf.jackfredlib.api.base.Memoizer;
import red.jackf.jackfredlib.client.api.toasts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static net.minecraft.network.chat.Component.literal;

public class ToastTest {
    private static int stoneMined = 0;

    private static final Supplier<CustomToast> TITLE_AND_MESSAGE = Memoizer.of(() -> ToastBuilder.builder(ToastFormat.DARK, literal("Test Toast"))
            .addMessage(literal("A fairly long message to stretch the toast height to get reach two slots instead of the default 1"))
            .withIcon(ToastIcon.image(ResourceLocation.fromNamespaceAndPath("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .progressPuller(toast -> Optional.of(toast.getProgress() + 0.005f))
            .rainbowProgressBar(true)
            .build());

    private static final Supplier<CustomToast> SHORT = Memoizer.of(() -> ToastBuilder.builder(ToastFormat.WHITE, literal("Test Toast 2"))
            .addMessage(literal("A shorter message"))
            .expiresAfter(3000L)
            .build());

    private static final Supplier<CustomToast> REPEATABLE_SHORT_SHARP = () -> ToastBuilder.builder(ToastFormat.WHITE_SHARP, literal("Repeatable Toast"))
            .addMessage(literal("0%"))
            .withIcon(ToastIcon.modIcon("jackfredlib"))
            .progressPuller(toast -> {
                var newProg = Math.min(toast.getProgress() + ((float) Math.random() / 150), 1f);
                toast.setMessage(List.of(literal("%.02f%%".formatted(newProg * 100))));
                if (newProg == 1f) {
                    toast.setTitle(literal("Complete"));
                    toast.setImage(null);
                }
                return Optional.of(newProg);
            })
            .expiresWhenProgressComplete(500L)
            .rainbowProgressBar(Math.random() < 0.5)
            .build();

    private static final Supplier<CustomToast> TALL_ALERT = Memoizer.of(() -> ToastBuilder.builder(ToastFormat.BLUE_ALERT, literal("Test Toast 3"))
            .addMessage(literal("A reaaaally long message so that the yellow exclamation mark to the left is only displayed once instead of repeating"))
            .addMessage(literal("Another message line"))
            .addMessage(literal("Staggered loading too!"))
            .withIcon(ToastIcon.modIcon("invalid"))
            .progressPuller(toast -> Optional.of(Math.random() < 0.03 ? toast.getProgress() + 0.1f : toast.getProgress()))
            .expiresWhenProgressComplete(2000L)
            .build());

    public static void setup() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClientSide || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            var stack = player.getItemInHand(hand);
            if (stack.is(Items.DIAMOND_PICKAXE)) {
                TITLE_AND_MESSAGE.get().setProgress(0f);
                Toasts.INSTANCE.send(TITLE_AND_MESSAGE.get());
            } else if (stack.is(Items.GOLDEN_PICKAXE)) {
                Toasts.INSTANCE.send(SHORT.get());
            } else if (stack.is(Items.IRON_PICKAXE)) {
                Toasts.INSTANCE.send(REPEATABLE_SHORT_SHARP.get());
            } else if (stack.is(Items.NETHERITE_PICKAXE)) {
                TALL_ALERT.get().setProgress(0f);
                Toasts.INSTANCE.send(TALL_ALERT.get());
            } else if (stack.is(Items.WOODEN_PICKAXE)) {
                var mods = new ArrayList<>(FabricLoader.getInstance().getAllMods());
                var mod = mods.get((int) (Math.random() * mods.size()));
                // get a random mod and send it's name and desc
                Toasts.INSTANCE.sendFromMod(mod.getMetadata().getId(), Component.nullToEmpty(mod.getMetadata().getDescription()));
            } else if (stack.is(Items.STONE_PICKAXE)) {
                Toasts.INSTANCE.send(ToastBuilder.builder(ToastFormat.BLUE_ALERT, literal("large image"))
                        .addMessage(literal("line ".repeat(6)))
                        .withIcon(ToastIcon.image(ResourceLocation.fromNamespaceAndPath("jackfredlib-testmod", "test_toast.png"), 2, 120, 120))
                        .progressPuller(toast -> {
                            if (toast.getTimeVisible() > 5000L) {
                                return Optional.of((float) (toast.getTimeVisible() - 5000L) / 5000L);
                            } else {
                                return Optional.empty();
                            }
                        })
                        .expiresWhenProgressComplete(1000L)
                        .build());
            } else if (stack.is(Items.WOODEN_SWORD)) {
                Toasts.INSTANCE.send(ToastBuilder.builder(ToastFormat.DARK, literal("Item Test"))
                        .withIcon(ToastIcon.item(Items.GOLDEN_PICKAXE.getDefaultInstance()))
                        .build());
            } else if (stack.is(Items.STONE_SWORD)) {
                Toasts.INSTANCE.send(ToastBuilder.builder(ToastFormat.WHITE_SHARP, literal("Item Test 2"))
                        .withIcon(ToastIcon.items(List.of(
                                Items.IRON_BLOCK.getDefaultInstance(),
                                Items.GOLD_BLOCK.getDefaultInstance(),
                                Items.DIAMOND_BLOCK.getDefaultInstance()
                        ), player.isCrouching() ? 2 : 1))
                        .progressShowsVisibleTime().build());
            }

            // jflib docs example
            else if (stack.is(Items.GOLD_INGOT)) {
                stoneMined = 0;

                Toasts.INSTANCE.send(ToastBuilder.builder(ToastFormat.DARK, literal("Mine 5 stone!"))
                        .withIcon(ToastIcon.item(Items.STONE.getDefaultInstance()))
                        .progressPuller(ignored -> Optional.of(Mth.clamp(((float) stoneMined / 5), 0, 1f)))
                        .expiresWhenProgressComplete(1000L)
                        .build());
            } else if (stack.is(Items.IRON_INGOT)) {
                List<ItemStack> ironArmour = List.of(
                        Items.IRON_HELMET.getDefaultInstance(),
                        Items.IRON_CHESTPLATE.getDefaultInstance(),
                        Items.IRON_LEGGINGS.getDefaultInstance(),
                        Items.IRON_BOOTS.getDefaultInstance()
                );

                var items = ToastBuilder.builder(ToastFormat.DARK, Component.literal("New Armour!"))
                        .addMessage(Component.literal("You have unlocked iron armour!"))
                        .withIcon(ToastIcon.items(ironArmour))
                        .progressShowsVisibleTime()
                        .expiresAfter(4000L)
                        .build();

                Toasts.INSTANCE.send(items);
            } else if (stack.is(Items.DIAMOND)) {
                var formats = Map.of(
                        ToastFormat.DARK, "DARK (advancements)",
                        ToastFormat.WHITE, "WHITE (recipe unlocks)",
                        ToastFormat.BLUE_ALERT, "BLUE_ALERT (system messages)",
                        ToastFormat.WHITE_SHARP, "WHITE_SHARP (tutorials)"
                );

                for (Map.Entry<ToastFormat, String> entry : formats.entrySet()) {
                    Toasts.INSTANCE.send(ToastBuilder.builder(entry.getKey(), literal(entry.getValue()))
                            .progressShowsVisibleTime()
                            .build());
                }
            }


            return InteractionResult.PASS;
        });

        ClientPlayerBlockBreakEvents.AFTER.register((world, player, pos, state) -> {
            if (state.is(Blocks.STONE)) {
                stoneMined = Math.min(5, stoneMined + 1);
            }
        });
    }
}

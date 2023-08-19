package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import red.jackf.jackfredlib.api.Memoizer;
import red.jackf.jackfredlib.client.api.toasts.*;

import java.util.Optional;
import java.util.function.Supplier;

public class ToastTest {
    private static final Supplier<CustomToast> TITLE_AND_MESSAGE = Memoizer.of(() -> CustomToast.builder(ToastFormat.DARK, Component.literal("Test Toast"))
            .withMessage(Component.literal("A fairly long message to stretch the toast height to get reach two slots instead of the default 1"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .progressPuller(toast -> Optional.of(toast.getProgress() + 0.005f))
            .rainbowProgressBar(true)
            .build());

    private static final Supplier<CustomToast> SHORT = Memoizer.of(() -> CustomToast.builder(ToastFormat.WHITE, Component.literal("Test Toast 2"))
            .withMessage(Component.literal("A shorter message"))
            .expiresAfter(7500L)
            .progressShowsVisibleTime()
            .build());

    private static final Supplier<CustomToast> REPEATABLE_SHORT_SHARP = () -> CustomToast.builder(ToastFormat.WHITE_SHARP, Component.literal("Repeatable Toast"))
            .withMessage(Component.literal("Shrt"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .progressPuller(toast -> Optional.of(toast.getProgress() + 0.005f))
            .expiresAfter(1500L)
            .rainbowProgressBar(Math.random() < 0.5)
            .build();

    private static final Supplier<CustomToast> TALL_ALERT = Memoizer.of(() -> CustomToast.builder(ToastFormat.BLUE_ALERT, Component.literal("Test Toast 3"))
            .withMessage(Component.literal("A reaaaally long message so that the yellow exclamation mark to the left is only displayed once instead of repeating"))
            .withMessage(Component.literal("Another message line"))
            .withMessage(Component.literal("Staggered loading too!"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .progressPuller(toast -> Optional.of(Math.random() < 0.03 ? toast.getProgress() + 0.1f : toast.getProgress()))
            .expiresWhenProgressComplete(2000L)
            .build());

    public static void setup() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClientSide || hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(ItemStack.EMPTY);
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
            }
            return InteractionResultHolder.pass(ItemStack.EMPTY);
        });
    }
}

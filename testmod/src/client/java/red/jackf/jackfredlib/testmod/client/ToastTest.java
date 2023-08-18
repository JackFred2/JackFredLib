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

import java.util.function.Supplier;

public class ToastTest {
    private static final Supplier<CustomToast> TITLE_AND_MESSAGE = Memoizer.of(() -> CustomToast.builder(ToastFormat.DARK, Component.literal("Test Toast"))
            .withMessage(Component.literal("A fairly long message to stretch the toast height to get reach two slots instead of the default 1"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .build());

    private static final Supplier<CustomToast> SHORT = Memoizer.of(() -> CustomToast.builder(ToastFormat.WHITE, Component.literal("Test Toast 2"))
            .withMessage(Component.literal("A shorter message"))
            .build());

    private static final Supplier<CustomToast> REPEATABLE_SHORT_SHARP = () -> CustomToast.builder(ToastFormat.WHITE_SHARP, Component.literal("Repeatable Toast"))
            .withMessage(Component.literal("Shrt"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .build();

    private static final Supplier<CustomToast> TALL_ALERT = Memoizer.of(() -> CustomToast.builder(ToastFormat.BLUE_ALERT, Component.literal("Test Toast 3"))
            .withMessage(Component.literal("A reaaaally long message so that the yellow exclamation mark to the left is only displayed once instead of repeating"))
            .withMessage(Component.literal("Another message line"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .build());

    public static void setup() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClientSide || hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(ItemStack.EMPTY);
            var stack = player.getItemInHand(hand);
            if (stack.is(Items.DIAMOND_PICKAXE)) {
                ToastManager.INSTANCE.send(TITLE_AND_MESSAGE.get());
            } else if (stack.is(Items.GOLDEN_PICKAXE)) {
                ToastManager.INSTANCE.send(SHORT.get());
            } else if (stack.is(Items.IRON_PICKAXE)) {
                ToastManager.INSTANCE.send(REPEATABLE_SHORT_SHARP.get());
            } else if (stack.is(Items.NETHERITE_PICKAXE)) {
                ToastManager.INSTANCE.send(TALL_ALERT.get());
            }
            return InteractionResultHolder.pass(ItemStack.EMPTY);
        });
    }
}

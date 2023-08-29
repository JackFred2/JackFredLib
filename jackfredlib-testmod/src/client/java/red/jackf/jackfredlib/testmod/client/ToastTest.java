package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import red.jackf.jackfredlib.api.Memoizer;
import red.jackf.jackfredlib.client.api.toasts.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static net.minecraft.network.chat.Component.literal;

public class ToastTest {
    private static final Supplier<CustomToast> TITLE_AND_MESSAGE = Memoizer.of(() -> ToastBuilder.builder(ToastFormat.DARK, literal("Test Toast"))
            .addMessage(literal("A fairly long message to stretch the toast height to get reach two slots instead of the default 1"))
            .withImage(ImageSpec.image(new ResourceLocation("jackfredlib-testmod", "test_toast.png"), 120, 120))
            .progressPuller(toast -> Optional.of(toast.getProgress() + 0.005f))
            .rainbowProgressBar(true)
            .build());

    private static final Supplier<CustomToast> SHORT = Memoizer.of(() -> ToastBuilder.builder(ToastFormat.WHITE, literal("Test Toast 2"))
            .addMessage(literal("A shorter message"))
            .expiresAfter(7500L)
            .progressShowsVisibleTime()
            .build());

    private static final Supplier<CustomToast> REPEATABLE_SHORT_SHARP = () -> ToastBuilder.builder(ToastFormat.WHITE_SHARP, literal("Repeatable Toast"))
            .addMessage(literal("0%"))
            .withImage(ImageSpec.modIcon("jackfredlib"))
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
            .withImage(ImageSpec.modIcon("invalid"))
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
            } else if (stack.is(Items.WOODEN_PICKAXE)) {
                var randomMod = new ArrayList<>(FabricLoader.getInstance().getAllMods());
                String randomId = randomMod.get((int) (Math.random() * randomMod.size())).getMetadata().getId();
                // get a random mod and send it with a random length message
                Toasts.INSTANCE.sendFromMod(randomId, literal("x ".repeat((int) (Math.random() * 30))));
            }
            return InteractionResultHolder.pass(ItemStack.EMPTY);
        });
    }
}

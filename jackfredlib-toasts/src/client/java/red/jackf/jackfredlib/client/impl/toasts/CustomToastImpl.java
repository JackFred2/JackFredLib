package red.jackf.jackfredlib.client.impl.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.api.toasts.CustomToast;
import red.jackf.jackfredlib.client.api.toasts.ImageSpec;
import red.jackf.jackfredlib.client.api.toasts.ToastFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CustomToastImpl implements CustomToast {
    private static final int DEFAULT_PADDING = 6;
    public static final int IMAGE_SIZE = 20;
    private final Object token = new Object();

    private final ToastFormat format;
    private Component title;
    private final List<FormattedCharSequence> messageLines = new ArrayList<>();
    private @Nullable ImageSpec image;
    private final VisibilityChecker visibiltyFunction;
    private final CustomToastBuilderImpl.ProgressPuller progressPuller;
    private final boolean rainbowProgress;

    private long visibleTimeStart = -1;
    private long visibleTime = -1;
    private long progressCompleteTime = -1;
    private float progress = 0;

    public CustomToastImpl(ToastFormat format,
                           Component title,
                           List<Component> messages,
                           @Nullable ImageSpec image,
                           VisibilityChecker visibiltyFunction,
                           CustomToastBuilderImpl.ProgressPuller progressPuller,
                           boolean rainbowProgress) {
        this.format = format;
        this.title = title;
        this.image = image;
        this.visibiltyFunction = visibiltyFunction;
        this.progressPuller = progressPuller;
        this.rainbowProgress = rainbowProgress;
        setMessage(messages);
    }

    public void setTitle(@NotNull Component title) {
        this.title = title;
    }

    public void setImage(@Nullable ImageSpec image) {
        this.image = image;
    }

    public void setMessage(List<Component> messages) {
        this.messageLines.clear();
        if (!messages.isEmpty()) {
            for (var message : messages) this.messageLines.addAll(Minecraft.getInstance().font.split(message, getMaxTextAreaWidth()));
        }
    }

    @Override
    public int width() {
        var font = Minecraft.getInstance().font;
        var width = IntStream.concat(IntStream.of(font.width(this.title)),
                this.messageLines.stream().mapToInt(font::width)).max().orElse(0);
        if (image != null) width += (IMAGE_SIZE + DEFAULT_PADDING);
        return width + DEFAULT_PADDING + format.leftWidth();
    }

    @Override
    public int height() {
        var font = Minecraft.getInstance().font;
        var total = 7 + (1 + messageLines.size()) * (font.lineHeight + 2);
        return Mth.positiveCeilDiv(total, 32) * 32;
    }

    // max width, minus left and right padding, minus another padding and the image size if an image is present
    private int getMaxTextAreaWidth() {
        return MAX_WIDTH - format.leftWidth() - DEFAULT_PADDING - (image != null ? IMAGE_SIZE + DEFAULT_PADDING : 0);
    }

    @Override
    public @NotNull Visibility render(GuiGraphics graphics, ToastComponent component, long timeVisible) {
        var newProgress = this.progressPuller.pull(this);
        newProgress.ifPresent(this::setProgress);

        visibleTime = timeVisible;
        if (visibleTimeStart == -1L) visibleTimeStart = timeVisible;
        if (progress >= 1f && progressCompleteTime == -1) progressCompleteTime = timeVisible;

        graphics.blitNineSliced(format.image(),
                0,
                0,
                width(),
                height(),
                format.leftWidth(),
                format.topHeight(),
                DEFAULT_PADDING,
                DEFAULT_PADDING,
                160,
                32,
                0,
                format.vOffset());

        var font = component.getMinecraft().font;
        var textX = format.leftWidth();

        if (image != null) {
            textX += IMAGE_SIZE + DEFAULT_PADDING;
            graphics.blit(image.location(),
                    format.leftWidth(), DEFAULT_PADDING,
                    IMAGE_SIZE, IMAGE_SIZE,
                    image.uOffset(), image.vOffset(),
                    image.uWidth(), image.vHeight(),
                    image.textureWidth(), image.textureHeight());
        }

        if (messageLines.isEmpty()) {
            graphics.drawString(font, title, textX, 12, format.titleColour(), false);
        } else {
            int textYStart = 7;
            graphics.drawString(font, title, textX, textYStart, format.titleColour(), false);
            for (int i = 1; i <= messageLines.size(); i++) {
                var line = messageLines.get(i - 1);
                var textY = textYStart + (i * 11);
                graphics.drawString(font, line, textX, textY, this.format.messageColour(), false);
            }
        }

        var progressBarWidth = width() - 2 * 3;
        var progressBarY = height() - 5;
        if (this.progress != 0f) {
            int colour = rainbowProgress ? 0xFF_000000 | Mth.hsvToRgb(progress / 3f, 1f, 1f) : format.progressBarColour();

            graphics.fill(3, progressBarY, 3 + (int) (progressBarWidth * progress), progressBarY + 2, colour);
        }

        return visibiltyFunction.check(this);
    }

    @Override
    public @NotNull Object getToken() {
        return token;
    }

    @Override
    public void refresh() {
        this.visibleTimeStart = -1;
        this.visibleTime = -1;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = Mth.clamp(progress, 0F, 1F);
        if (this.progress < 1f) this.progressCompleteTime = -1;
    }

    public long getTimeSinceProgressComplete() {
        return progress == 1f ? visibleTime - progressCompleteTime : -1;
    }

    public long getTimeVisible() {
        return visibleTime - visibleTimeStart;
    }

    public interface VisibilityChecker {
        Visibility check(CustomToastImpl customToast);
    }
}

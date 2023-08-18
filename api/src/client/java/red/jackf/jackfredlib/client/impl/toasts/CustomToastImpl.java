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
    private static final int MAX_WIDTH = 240;
    private static final int DEFAULT_PADDING = 6;
    private static final int IMAGE_SIZE = 20;
    private final Object token = new Object();

    private final ToastFormat format;
    private Component title;
    private final List<FormattedCharSequence> messageLines = new ArrayList<>();
    private @Nullable ImageSpec image;

    public CustomToastImpl(ToastFormat format, Component title, List<Component> messages, @Nullable ImageSpec image) {
        this.format = format;
        this.title = title;
        this.image = image;
        setMessage(messages);
    }

    public void setTitle(Component title) {
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

        return timeVisible > 5000L ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public @NotNull Object getToken() {
        return token;
    }

    @Override
    public void refresh() {

    }
}

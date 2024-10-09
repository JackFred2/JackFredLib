package red.jackf.jackfredlib.api.lying.entity.builders.display;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.mixins.lying.entity.TextDisplayAccessor;

/**
 * Builder to create a text display, which shows a {@link net.minecraft.network.chat.Component}.
 *
 * @see <a href="https://minecraft.fandom.com/wiki/Display">Minecraft Wiki: Display</a>
 */
public class TextDisplayBuilder extends DisplayBuilder<Display.TextDisplay, TextDisplayBuilder> {
    /**
     * Create a new Text Display builder. Don't use directly, use a method in {@link red.jackf.jackfredlib.api.lying.entity.builders.EntityBuilders}.
     *
     * @param level Level to create the fake entity in.
     */
    @ApiStatus.Internal
    public TextDisplayBuilder(ServerLevel level) {
        super(EntityType.TEXT_DISPLAY, level);
    }

    /**
     * Set the text to be displayed.
     *
     * @param text Text to display
     * @return This entity builder
     */
    public TextDisplayBuilder text(Component text) {
        ((TextDisplayAccessor) this.entity).jflib$setText(text);
        return self();
    }

    /**
     * Set the text alignment of this display, in the case of multiple lines.
     *
     * @param align Alignment to use for the left
     * @return This entity builder
     * @implNote Default: {@link Display.TextDisplay.Align#CENTER}
     * @see #lineWidth(int)
     */
    public TextDisplayBuilder textAlign(Display.TextDisplay.Align align) {
        byte flags = (byte) (((TextDisplayAccessor) this.entity).jflib$getFlags() & 0b00111);
        if (align == Display.TextDisplay.Align.LEFT) flags |= Display.TextDisplay.FLAG_ALIGN_LEFT;
        else if (align == Display.TextDisplay.Align.RIGHT) flags |= Display.TextDisplay.FLAG_ALIGN_RIGHT;
        ((TextDisplayAccessor) this.entity).jflib$setFlags(flags);
        return self();
    }

    /**
     * Sets the maximum line width of this text display before it enters a new line. This does not mean max characters,
     * rather it means the width returned by <code>net.minecraft.client.gui.Font#width(Component)</code>.
     *
     * @param width Maximum width of the text display before splitting lines.
     * @return This entity builder
     * @implNote Default: 200
     */
    public TextDisplayBuilder lineWidth(int width) {
        ((TextDisplayAccessor) this.entity).jflib$setLineWidth(width);
        return self();
    }

    /**
     * <p>Sets the transparency of the text on the display, in the range [0, 255] from fully transparent to fully opaque.
     * Does not affect the background transparency.</p>
     *
     * <p>Values below 26 are equivalent to fully transparent.</p>
     *
     * @param opacity Opacity to set the opacity to.
     * @return This entity builder
     * @implNote Default: 255 (fully opaque).
     * @see #backgroundColour(int)
     */
    public TextDisplayBuilder textOpacity(int opacity) {
        ((TextDisplayAccessor) this.entity).jflib$setTextOpacity((byte) Mth.clamp(opacity, 0, 255));
        return self();
    }

    /**
     * <p>Sets the colour and transparency of the text background block.</p>
     *
     * <p>Values below 26 are equivalent to fully transparent.</p>
     *
     * @param argb Colour to set the text background to
     * @return This entity builder
     * @implNote Default: 0x40_000000 (25% transparent black).
     * @see #textOpacity(int)
     */
    public TextDisplayBuilder backgroundColour(int argb) {
        ((TextDisplayAccessor) this.entity).jflib$setBackgroundColor(argb);
        return self();
    }

    /**
     * <p>Sets the colour and transparency of the text background block.</p>
     *
     * <p>Values below 26 are equivalent to fully transparent.</p>
     *
     * @param a Alpha value of the background colour, in the range [0, 255]. Values less than 26 are equivalent to 0.
     * @param r Red value of the background colour.
     * @param g Green value of the background colour.
     * @param b Blue value of the background colour.
     * @return This entity builder
     * @implNote Default: 0x40_000000 (25% transparent black).
     * @see #textOpacity(int)
     */
    public TextDisplayBuilder backgroundColour(int a, int r, int g, int b) {
        return backgroundColour(ARGB.color(a, r, g, b));
    }

    /**
     * <p>Sets the colour and transparency of the text background block.</p>
     *
     * <p>Values below 26 are equivalent to fully transparent.</p>
     *
     * @param colour Colour for the text background
     * @return This entity builder
     * @implNote Default: 0x40_000000 (25% transparent black).
     * @see #textOpacity(int)
     */
    public TextDisplayBuilder backgroundColour(Colour colour) {
        return backgroundColour(colour.toARGB());
    }

    /**
     * <p>Sets if this text display should defer to a client's background transparency (in accessibility), with a black
     * background.</p>
     *
     * <p>Overrides {@link #backgroundColour(int)}.</p>
     *
     * @param useDefaultBackground Whether to defer to a client's background settings
     * @return This entity builder
     * @implNote Default: false
     */
    public TextDisplayBuilder useDefaultBackground(boolean useDefaultBackground) {
        ((TextDisplayAccessor) this.entity).jflib$setFlags(withFlag(((TextDisplayAccessor) this.entity).jflib$getFlags(), Display.TextDisplay.FLAG_USE_DEFAULT_BACKGROUND, useDefaultBackground));
        return self();
    }

    /**
     * <p>Sets if this text display should render through terrain.</p>
     *
     * <p>Does not function correctly through some block entities such as chests; you may have better rendering results
     * with {@link #customName(Component)} and {@link #alwaysRenderName(boolean)} instead of a text display if you're
     * using a billboard mode of {@link net.minecraft.world.entity.Display.BillboardConstraints#CENTER}.</p>
     *
     * @param seeThrough Whether this text display should render through terrain
     * @return This entity builder
     * @implNote Default: false
     */
    public TextDisplayBuilder seeThrough(boolean seeThrough) {
        ((TextDisplayAccessor) this.entity).jflib$setFlags(withFlag(((TextDisplayAccessor) this.entity).jflib$getFlags(), Display.TextDisplay.FLAG_SEE_THROUGH, seeThrough));
        return self();
    }

    /**
     * <p>Sets if this text display should display a shadow.</p>
     *
     * @param hasShadow Whether this text display should render through terrain
     * @return This entity builder
     * @implNote Default: false
     */
    public TextDisplayBuilder hasShadow(boolean hasShadow) {
        ((TextDisplayAccessor) this.entity).jflib$setFlags(withFlag(((TextDisplayAccessor) this.entity).jflib$getFlags(), Display.TextDisplay.FLAG_SHADOW, hasShadow));
        return self();
    }

    public static byte withFlag(byte field, byte flag, boolean shouldSet) {
        if (shouldSet) return (byte) (field | flag);
        else return (byte) (field & ~flag);
    }

    @Override
    protected TextDisplayBuilder self() {
        return this;
    }
}

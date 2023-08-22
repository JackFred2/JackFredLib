package red.jackf.jackfredlib.api.extracommandsourcedata;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * An arbitrary data class to attach to a {@link net.minecraft.commands.CommandSourceStack}. These can be accessed and
 * modified through {@link com.mojang.brigadier.builder.ArgumentBuilder#fork(CommandNode, RedirectModifier)} or
 * {@link com.mojang.brigadier.builder.ArgumentBuilder#redirect(CommandNode, SingleRedirectModifier)}.
 *
 * @param <T> Type of the class being built; same as the class name.
 */
public interface ExtraSourceData<T extends ExtraSourceData<T>> {
    /**
     * Create a <b>deep copy</b> of this data class; such that modifying the copy will not modify the first. Generally,
     * this means copying any collections instead of passing by reference.
     *
     * @return The created copy.
     */
    T copy();

    /**
     * A definition for an extra data class; save this to a static final field as you will access your custom data via
     * this.
     *
     * @param id Unique ID for this extra source data.
     * @param clazz Class of your data class.
     * @param factory Supplier for a new instance of your data class.
     * @param <T> Class of your data class.
     */
    record Definition<T extends ExtraSourceData<T>>(ResourceLocation id, Class<T> clazz, Supplier<T> factory) { }
}

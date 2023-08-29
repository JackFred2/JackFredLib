package red.jackf.jackfredlib.api.extracommandsourcedata;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import red.jackf.jackfredlib.impl.extrasourcedata.CommandSourceStackUtils;
import red.jackf.jackfredlib.impl.extrasourcedata.ExtraCommandSourceStackDataDuck;

import java.util.function.Consumer;

/**
 * Set of utility functions for accessing and modifying custom source data.
 */
public class ESD {
    /**
     * Create a copy of the given source stack. For this copy, get or create a custom source data class, then run a
     * modifier on said class. Intended to be used in a similar fashion to a <code>with</code> method from
     * {@link CommandSourceStack}.
     *
     * @param source       Command source to copy and modify
     * @param definition   Source data definition to create or modify.
     * @param dataModifier Function ran on the given custom data class.
     * @param <T>          Class of the custom data class.
     * @return Copy of the given source with the modified custom data class.
     * @throws CommandSyntaxException If there already exists a custom data class by the given definition's ID, but it is
     *                                not an instance of the definition's class.
     */
    public static <T extends ExtraSourceData<T>> CommandSourceStack withCustom(CommandSourceStack source,
                                                                               ExtraSourceData.Definition<T> definition,
                                                                               Consumer<T> dataModifier) throws CommandSyntaxException {
        var copy = CommandSourceStackUtils.copy(source);
        var data = getCustom(copy, definition);
        dataModifier.accept(data);
        return copy;
    }

    /**
     * Syntactic sugar for {@link #withCustom(CommandSourceStack, ExtraSourceData.Definition, Consumer)}
     * <p>
     * Create a copy of the given context's source stack. For this copy, get or create a custom source data class, then run a
     * modifier on said class. Intended to be used in a similar fashion to a <code>with</code> method from
     * {@link CommandSourceStack}.
     *
     * @param ctx          Command context whose source stack will be copied and modified.
     * @param definition   Source data definition to create or modify.
     * @param dataModifier Function ran on the given custom data class.
     * @param <T>          Class of the custom data class.
     * @return Copy of the given source with the modified custom data class.
     * @throws CommandSyntaxException If there already exists a custom data class by the given definition's ID, but it is
     *                                not an instance of the definition's class.
     **/
    public static <T extends ExtraSourceData<T>> CommandSourceStack withCustom(CommandContext<CommandSourceStack> ctx,
                                                                               ExtraSourceData.Definition<T> definition,
                                                                               Consumer<T> dataModifier) throws CommandSyntaxException {
        return withCustom(ctx.getSource(), definition, dataModifier);
    }

    /**
     * Retrieve a custom data class from the given command stack source. Generally used in your {@link com.mojang.brigadier.builder.ArgumentBuilder#executes(Command)}
     * methods.
     *
     * @param source     Command source to copy and modify
     * @param definition Source data definition to retrieve.
     * @param <T>        Class of the custom data class.
     * @return Instance of your data class from the given command source. A default instance is returned if none was found.
     * @throws CommandSyntaxException If there exists a custom data class by the given definition's ID, but it is
     *                                not an instance of the definition's class.
     */
    public static <T extends ExtraSourceData<T>> T getCustom(CommandSourceStack source,
                                                             ExtraSourceData.Definition<T> definition) throws CommandSyntaxException {
        return ((ExtraCommandSourceStackDataDuck) source).jackfredlib$getData(definition);
    }


    /**
     * Syntactic sugar for {@link #getCustom(CommandSourceStack, ExtraSourceData.Definition)}
     * <p>
     * Retrieve a custom data class from the given command stack source. Generally used in your {@link com.mojang.brigadier.builder.ArgumentBuilder#executes(Command)}
     * methods.
     *
     * @param ctx        Command context whose source stack will be read
     * @param definition Source data definition to retrieve.
     * @param <T>        Class of the custom data class.
     * @return Instance of your data class from the given command source. A default instance is returned if none was found.
     * @throws CommandSyntaxException If there exists a custom data class by the given definition's ID, but it is
     *                                not an instance of the definition's class.
     */
    public static <T extends ExtraSourceData<T>> T getCustom(CommandContext<CommandSourceStack> ctx,
                                                             ExtraSourceData.Definition<T> definition) throws CommandSyntaxException {
        return getCustom(ctx.getSource(), definition);
    }
}

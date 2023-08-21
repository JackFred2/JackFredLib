package red.jackf.jackfredlib.api.extrasourcedata;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import red.jackf.jackfredlib.impl.extrasourcedata.CommandSourceStackUtils;
import red.jackf.jackfredlib.impl.extrasourcedata.JFLibCommandSourceStackDuck;

import java.util.function.Consumer;

public class ESD {
    public static <T extends ExtraSourceData<T>> CommandSourceStack withCustom(CommandSourceStack source,
                                                                               ExtraSourceData.Definition<T> definition,
                                                                               Consumer<T> dataModifier) throws CommandSyntaxException {
        var copy = CommandSourceStackUtils.copy(source);
        var data = getCustom(copy, definition);
        dataModifier.accept(data);
        return copy;
    }

    public static <T extends ExtraSourceData<T>> CommandSourceStack withCustom(CommandContext<CommandSourceStack> ctx,
                                                                               ExtraSourceData.Definition<T> definition,
                                                                               Consumer<T> dataModifier) throws CommandSyntaxException {
        return withCustom(ctx.getSource(), definition, dataModifier);
    }

    public static <T extends ExtraSourceData<T>> T getCustom(CommandSourceStack source,
                                                             ExtraSourceData.Definition<T> definition) throws CommandSyntaxException {
        return ((JFLibCommandSourceStackDuck) source).jackfredlib$getData(definition);
    }

    public static <T extends ExtraSourceData<T>> T getCustom(CommandContext<CommandSourceStack> ctx,
                                                             ExtraSourceData.Definition<T> definition) throws CommandSyntaxException {
        return ((JFLibCommandSourceStackDuck) ctx.getSource()).jackfredlib$getData(definition);
    }
}

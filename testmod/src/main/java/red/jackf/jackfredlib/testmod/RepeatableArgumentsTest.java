package red.jackf.jackfredlib.testmod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import red.jackf.jackfredlib.api.extracommandsourcedata.ESD;
import red.jackf.jackfredlib.api.extracommandsourcedata.ExtraSourceData;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RepeatableArgumentsTest {
    private static final ExtraSourceData.Definition<MutliArgData> DEFINITION = new ExtraSourceData.Definition<>(
            new ResourceLocation("jackfredlib-testmod", "repeated_args"),
            MutliArgData.class,
            MutliArgData::new
    );

    static void setup() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralCommandNode<CommandSourceStack> root = dispatcher.register(literal("repeatableArgs"));

            dispatcher.register(literal("repeatableArgs").then(
                    literal("int").then(
                            argument("int", IntegerArgumentType.integer())
                                    .redirect(root, ctx -> ESD.withCustom(ctx, DEFINITION, multiArg ->
                                            multiArg.ints.add(ctx.getArgument("int", Integer.class))
                                    ))
                    )).then(
                    literal("str").then(
                            argument("str", StringArgumentType.word())
                                    .redirect(root, ctx -> ESD.withCustom(ctx, DEFINITION, multiArg ->
                                            multiArg.strs.add(ctx.getArgument("str", String.class))
                                    ))
                    )).then(
                    literal("go").executes(RepeatableArgumentsTest::execute)
            ));
        });
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var custom = ESD.getCustom(ctx, DEFINITION);
        ctx.getSource().getPlayerOrException()
                .sendSystemMessage(Component.literal("ints: " + custom.ints + ", strs: " + custom.strs));
        return 0;
    }

    private static class MutliArgData implements ExtraSourceData<MutliArgData> {
        private final List<Integer> ints = new ArrayList<>();
        private final List<String> strs = new ArrayList<>();

        public MutliArgData() {
        }

        public MutliArgData(MutliArgData other) {
            this.ints.addAll(other.ints);
            this.strs.addAll(other.strs);
        }

        @Override
        public MutliArgData copy() {
            return new MutliArgData(this);
        }

        @Override
        public String toString() {
            return "MutliArgData{" +
                    "ints=" + ints +
                    ", strs=" + strs +
                    '}';
        }
    }
}

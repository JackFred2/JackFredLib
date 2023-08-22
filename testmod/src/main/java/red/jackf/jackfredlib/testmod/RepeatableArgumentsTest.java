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
                    literal("arg1").then(
                            argument("arg1", IntegerArgumentType.integer())
                                    .redirect(root, ctx -> ESD.withCustom(ctx, DEFINITION, multiArg ->
                                            multiArg.arg1s.add(ctx.getArgument("arg1", Integer.class))
                                    ))
                    )).then(
                    literal("arg2").then(
                            argument("arg2", StringArgumentType.word())
                                    .redirect(root, ctx -> ESD.withCustom(ctx, DEFINITION, multiArg ->
                                            multiArg.arg2s.add(ctx.getArgument("arg2", String.class))
                                    ))
                    )).then(
                    literal("go").executes(RepeatableArgumentsTest::execute)
            ));
        });
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        var custom = ESD.getCustom(ctx, DEFINITION);
        ctx.getSource().getPlayerOrException()
                .sendSystemMessage(Component.literal("arg1: " + custom.arg1s + ", arg2: " + custom.arg2s));
        return 0;
    }

    private static class MutliArgData implements ExtraSourceData<MutliArgData> {
        private final List<Integer> arg1s = new ArrayList<>();
        private final List<String> arg2s = new ArrayList<>();

        public MutliArgData() {
        }

        public MutliArgData(MutliArgData other) {
            this.arg1s.addAll(other.arg1s);
            this.arg2s.addAll(other.arg2s);
        }

        @Override
        public MutliArgData copy() {
            return new MutliArgData(this);
        }

        @Override
        public String toString() {
            return "MutliArgData{" +
                    "arg1s=" + arg1s +
                    ", arg2s=" + arg2s +
                    '}';
        }
    }
}

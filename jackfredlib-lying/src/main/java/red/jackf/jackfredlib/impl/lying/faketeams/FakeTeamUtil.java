package red.jackf.jackfredlib.impl.lying.faketeams;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.api.base.ServerTracker;

import java.util.Arrays;
import java.util.List;

public class FakeTeamUtil {
    private FakeTeamUtil() {}

    static final List<ChatFormatting> COLOURS = Arrays.stream(ChatFormatting.values())
            .filter(ChatFormatting::isColor)
            .toList();

    @Contract("null -> null; !null -> !null")
    public static @Nullable ChatFormatting ensureValidColour(@Nullable ChatFormatting colour) {
        if (colour == null) return null;
        if (!colour.isColor()) return ChatFormatting.WHITE;
        return colour;
    }

    @Nullable
    static ClientboundSetPlayerTeamPacket.Parameters createFakeParameters(ChatFormatting colour) {
        var server = ServerTracker.INSTANCE.getServer();
        if (server == null) return null;
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());

        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, Component.literal(getName(colour)));
        buf.writeByte(0);
        buf.writeUtf(Team.Visibility.ALWAYS.name);
        buf.writeUtf(Team.CollisionRule.ALWAYS.name);
        buf.writeEnum(colour);
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, Component.empty());
        ComponentSerialization.TRUSTED_STREAM_CODEC.encode(buf, Component.empty());

        return new ClientboundSetPlayerTeamPacket.Parameters(buf);
    }

    static String getName(ChatFormatting colour) {
        return "JFLIB_LYING_" + colour.name();
    }
}

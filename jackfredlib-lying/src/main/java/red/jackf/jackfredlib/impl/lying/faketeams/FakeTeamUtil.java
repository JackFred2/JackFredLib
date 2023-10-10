package red.jackf.jackfredlib.impl.lying.faketeams;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FakeTeamUtil {
    static final List<ChatFormatting> COLOURS = Arrays.stream(ChatFormatting.values())
            .filter(ChatFormatting::isColor)
            .toList();

    static final Map<ChatFormatting, ClientboundSetPlayerTeamPacket.Parameters> FAKE_PARAMETERS = generateFakeParameters();

    private static Map<ChatFormatting, ClientboundSetPlayerTeamPacket.Parameters> generateFakeParameters() {
        var map = new HashMap<ChatFormatting, ClientboundSetPlayerTeamPacket.Parameters>(16);

        for (ChatFormatting colour : COLOURS) {
            String name = getName(colour);

            var fakeBuf = new FriendlyByteBuf(Unpooled.buffer());
            fakeBuf.writeComponent(Component.literal(name)); // display name
            fakeBuf.writeByte(0); // options
            fakeBuf.writeUtf(Team.Visibility.ALWAYS.name); // team visibility
            fakeBuf.writeUtf(Team.CollisionRule.ALWAYS.name); // collision

            fakeBuf.writeEnum(colour); // colour

            fakeBuf.writeComponent(CommonComponents.EMPTY); // prefix
            fakeBuf.writeComponent(CommonComponents.EMPTY); // suffix

            map.put(colour, new ClientboundSetPlayerTeamPacket.Parameters(fakeBuf));
        }

        return map;
    }

    static String getName(ChatFormatting colour) {
        return "JFLIB_LYING_" + colour.name();
    }
}

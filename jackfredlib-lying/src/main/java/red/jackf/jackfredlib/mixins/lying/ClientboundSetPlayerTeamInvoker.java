package red.jackf.jackfredlib.mixins.lying;

import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Collection;
import java.util.Optional;

@Mixin(ClientboundSetPlayerTeamPacket.class)
public interface ClientboundSetPlayerTeamInvoker {

    @Invoker(value = "<init>")
    static ClientboundSetPlayerTeamPacket createManually(
            String name,
            int method,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<ClientboundSetPlayerTeamPacket.Parameters> parameters,
            Collection<String> players) {
        throw new AssertionError("mixin method called");
    };
}

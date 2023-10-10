package red.jackf.jackfredlib.impl.lying.faketeams;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import red.jackf.jackfredlib.mixins.lying.ClientboundSetPlayerTeamInvoker;

import java.util.Optional;

public enum FakeTeamManager {
    INSTANCE;

    private static final int CREATE_TEAM = 0;
    private static final int REMOVE_TEAM = 1;
    private static final int ADD_ENTITY = 3;
    private static final int REMOVE_ENTITY = 4;

    private final Multimap<ChatFormatting, GameProfile> visible
            = MultimapBuilder.enumKeys(ChatFormatting.class).hashSetValues().build();

    public void disconnect(GameProfile profile) {
        for (var colour : FakeTeamUtil.COLOURS)
            visible.get(colour).remove(profile);
    }

    public void addToTeam(ServerPlayer player, Entity entity, ChatFormatting colour) {
        if (!colour.isColor()) colour = ChatFormatting.WHITE;

        String name = entity.getScoreboardName();

        if (!visible.get(colour).contains(player.getGameProfile())) {
            var packet = ClientboundSetPlayerTeamInvoker.createManually(
                    FakeTeamUtil.getName(colour),
                    CREATE_TEAM,
                    Optional.of(FakeTeamUtil.FAKE_PARAMETERS.get(colour)),
                    ImmutableList.of(entity.getScoreboardName())
            );

            player.connection.send(packet);
            visible.put(colour, player.getGameProfile());
        } else {
            var packet = ClientboundSetPlayerTeamInvoker.createManually(
                    FakeTeamUtil.getName(colour),
                    ADD_ENTITY,
                    Optional.empty(),
                    ImmutableList.of(entity.getScoreboardName())
            );

            player.connection.send(packet);
        }
    }

    public void removeFromTeam(ServerPlayer player, Entity entity, ChatFormatting colour) {
        if (!colour.isColor()) colour = ChatFormatting.WHITE;

        if (visible.get(colour).contains(player.getGameProfile())) {
            var packet = ClientboundSetPlayerTeamInvoker.createManually(
                    FakeTeamUtil.getName(colour),
                    REMOVE_ENTITY,
                    Optional.empty(),
                    ImmutableList.of(entity.getScoreboardName())
            );

            player.connection.send(packet);
        }
    }
}

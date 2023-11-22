package red.jackf.jackfredlib.client.impl.gps;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.*;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.api.gps.PlayerListSnapshot;
import red.jackf.jackfredlib.client.api.gps.ScoreboardSnapshot;
import red.jackf.jackfredlib.client.mixins.gps.PlayerTabOverlayAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GPSUtilImpl {
    public static PlayerListSnapshot getPlayerList() {
        PlayerTabOverlay tabList = Minecraft.getInstance().gui.getTabList();
        PlayerTabOverlayAccessor accessed = (PlayerTabOverlayAccessor) Minecraft.getInstance().gui.getTabList();
        Optional<Component> header = Optional.ofNullable(accessed.jflib$getHeader());
        Optional<Component> footer = Optional.ofNullable(accessed.jflib$getFooter());
        List<Component> names = accessed.jflib$getPlayerInfos().stream()
                                        .map(tabList::getNameForDisplay)
                                        .toList();
        return new PlayerListSnapshotImpl(header, footer, names);
    }

    public static Optional<ScoreboardSnapshot> getScoreboard() {
        Objective obj = getDisplayedScoreboardObjective();
        if (obj == null) return Optional.empty();
        Scoreboard scoreboard = obj.getScoreboard();

        final int LIMIT = 15;

        Collection<Score> rawScores = scoreboard.getPlayerScores(obj);
        //vanilla code checks for null so we do too
        //noinspection ConstantValue
        List<Score> scores = rawScores.stream()
                                      .filter(score -> score.getOwner() != null && !score.getOwner().startsWith("#"))
                                      .toList();

        if (scores.isEmpty()) return Optional.empty();

        if (scores.size() > LIMIT) scores = Lists.newArrayList(Iterables.skip(scores, rawScores.size() - LIMIT));

        List<Pair<Component, Component>> lines = new ArrayList<>(scores.size());

        for (int i = scores.size() - 1; i >= 0; i--) {
            Score score = scores.get(i);
            PlayerTeam team = obj.getScoreboard().getPlayersTeam(score.getOwner());
            Component formattedName = PlayerTeam.formatNameForTeam(team, Component.literal(score.getOwner()));
            Component formattedScore = Component.literal(String.valueOf(score.getScore())).withStyle(ChatFormatting.RED);
            lines.add(Pair.of(formattedName, formattedScore));
        }

        return Optional.of(new ScoreboardSnapshotImpl(obj.getDisplayName(), lines));
    }

    private static @Nullable Objective getDisplayedScoreboardObjective() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return null;
        var scoreboard = mc.level.getScoreboard();
        Objective obj = null;

        var playerTeam = scoreboard.getPlayerTeam(mc.player.getScoreboardName());

        if (playerTeam != null) {
            int colourIndex = playerTeam.getColor().getId();
            if (colourIndex >= 0) obj = scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_TEAMS_SIDEBAR_START + colourIndex);
        }

        if (obj == null) obj = scoreboard.getDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR);

        return obj;
    }
}

package red.jackf.jackfredlib.client.impl.gps;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import org.jetbrains.annotations.Nullable;
import red.jackf.jackfredlib.client.mixins.gps.PlayerTabOverlayAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GPSUtilImpl {
    public static @Nullable String getPlayerListHeader() {
        var header = ((PlayerTabOverlayAccessor) Minecraft.getInstance().gui.getTabList()).jflib$getHeader();
        return header != null ? header.getString() : null;
    }
    public static @Nullable String getPlayerListFooter() {
        var footer = ((PlayerTabOverlayAccessor) Minecraft.getInstance().gui.getTabList()).jflib$getFooter();
        return footer != null ? footer.getString() : null;
    }

    public static List<String> getPlayerList() {
        var tabList = Minecraft.getInstance().gui.getTabList();
        return ((PlayerTabOverlayAccessor) tabList).jflib$getPlayerInfos().stream()
                .map(info -> tabList.getNameForDisplay(info).getString())
                .toList();
    }

    public static List<String> getScoreboard() {
        var obj = getDisplayedScoreboardObjective();
        if (obj == null) return Collections.emptyList();
        //noinspection ConstantValue
        var scores = obj.getScoreboard().getPlayerScores(obj).stream()
                .filter(score -> score.getOwner() != null && !score.getOwner().startsWith("#"))
                .toList();
        if (scores.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(scores, scores.size() - 15));
        }
        if (scores.isEmpty()) return Collections.emptyList();
        List<String> lines = new ArrayList<>(scores.size() + 1);
        lines.add(obj.getDisplayName().getString());
        for (int i = scores.size() - 1; i >= 0; i--) {
            Score score = scores.get(i);
            PlayerTeam team = obj.getScoreboard().getPlayersTeam(score.getOwner());
            Component formattedName = PlayerTeam.formatNameForTeam(team, Component.literal(score.getOwner()));
            lines.add(formattedName.getString());
        }
        return lines;
    }

    private static @Nullable Objective getDisplayedScoreboardObjective() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return null;
        var scoreboard = mc.level.getScoreboard();
        Objective obj = null;
        var playerTeam = scoreboard.getPlayerTeam(mc.player.getScoreboardName());
        if (playerTeam != null) {
            var displaySlot = DisplaySlot.teamColorToSlot(playerTeam.getColor());
            if (displaySlot != null) obj = scoreboard.getDisplayObjective(displaySlot);
        }
        if (obj == null) obj = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
        return obj;
    }
}

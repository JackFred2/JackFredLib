package red.jackf.jackfredlib.client.api.gps;

import com.google.common.collect.Streams;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.client.impl.gps.GPSUtilImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>Utilities for interacting with the in-game scoreboard on the right-side of the screen. Some servers use this to
 * provide additional information, which can be used by client mods.</p>
 *
 * <p>Deprecated, please use {@link ScoreboardSnapshot#take()}, and the given methods.</p>
 */
@Deprecated(since = "1.0.5", forRemoval = true)
public class ScoreboardUtils {
    /**
     * Return all names for the displayed scoreboard, or an empty list if not displayed. Entry order is top down, including
     * the header at index 0.
     *
     * @return All displayed rows on the scoreboard.
     */
    public static List<String> getRows() {
        return GPSUtilImpl.getScoreboard()
                          .map(snapshot -> Streams.concat(Stream.of(snapshot.title()),
                                                          snapshot.names().stream())
                                                  .map(Component::getString)
                                                  .toList())
                          .orElse(Collections.emptyList());
    }

    /**
     * Returns the contents of a scoreboard row which is prefixed with a string, with said prefix removed, or an empty
     * optional otherwise.
     *
     * @param prefix Prefix to filter by and strip.
     * @return An Optional containing a string which was prefixed by <code>prefix</code>, or an empty optional if not
     * found.
     */
    public static Optional<String> getPrefixed(String prefix) {
        return GPSUtilImpl.getScoreboard().flatMap(snapshot -> {
            if (snapshot.title().getString().startsWith(prefix)) return Optional.of(snapshot.title().getString().substring(prefix.length()));
            return snapshot.nameWithPrefixStripped(prefix);
        });
    }

    /**
     * Get an optional containing the specified row on the scoreboard if present, or an empty optional if not present.
     *
     * @param row Row to lookup. 0-indexed from top to bottom, including the header, or can be negative to search from
     *            the bottom up instead. For example, passing <code>0</code> returns the header, while <code>-1</code>
     *            returns the bottom row.
     * @return The specified row on the scoreboard, or an empty optional if out of bounds.
     */
    public static Optional<String> getRow(int row) {
        return GPSUtilImpl.getScoreboard().map(snapshot -> {
            int size = 1 + snapshot.entries().size();

            int targetedRow = row < 0 ? size + row : row;
            if (targetedRow < 0 || targetedRow >= size) return null;

            if (targetedRow == 0) return snapshot.title().getString();
            return snapshot.entryFromTop(targetedRow - 1).map(pair -> pair.getFirst().getString()).orElse(null);
        });
    }
}

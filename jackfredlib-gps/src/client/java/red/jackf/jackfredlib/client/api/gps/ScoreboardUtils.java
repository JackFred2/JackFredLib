package red.jackf.jackfredlib.client.api.gps;

import red.jackf.jackfredlib.client.impl.gps.GPSUtilImpl;

import java.util.List;
import java.util.Optional;

/**
 * <p>Utilities for interacting with the in-game scoreboard on the right-side of the screen. Some servers use this to
 * provide additional information, which can be used by client mods.</p>
 */
public class ScoreboardUtils {
    /**
     * Return all rows for the displayed scoreboard, or an empty list if not displayed. Entry order is top down, including
     * the header at index 0.
     *
     * @return All displayed rows on the scoreboard.
     */
    public static List<String> getRows() {
        return GPSUtilImpl.getScoreboard();
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
        return GPSUtilImpl.getScoreboard().stream()
                .filter(s -> s.startsWith(prefix))
                .findFirst()
                .map(s -> s.substring(prefix.length()));
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
        var rows = GPSUtilImpl.getScoreboard();
        if (rows.isEmpty()) return Optional.empty();
        int targetedRow = row < 0 ? rows.size() + row : row;
        if (targetedRow < 0 || targetedRow >= rows.size()) return Optional.empty();
        return Optional.of(rows.get(targetedRow));
    }
}

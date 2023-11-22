package red.jackf.jackfredlib.client.api.gps;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.client.impl.gps.GPSUtilImpl;

import java.util.List;
import java.util.Optional;

/**
 * <p>Represents a snapshot of the right-hand scoreboard currently displayed to the client.</p>
 *
 * <p>All examples below will use the following scoreboard as an example:</p>
 *
 * <pre>
 *     +-------------------------+
 *     |     LARGESERVER.COM     |
 *     | Autumn 12th           7 |
 *     | 9:20am                6 |
 *     |                       5 |
 *     | Area: Mines           4 |
 *     | Balance: 400          3 |
 *     |                       2 |
 *     | www.largeserver.net   1 |
 *     +-------------------------+
 * </pre>
 *
 * @see Component#getString()
 */
public interface ScoreboardSnapshot {
    /**
     * Take a snapshot of the currently displayed player scoreboard, on the right hand of the screen. Returns an empty optional if
     * the scoreboard is not currently displayed.
     *
     * @return An optional containing a snapshot of the current scoreboard, or an empty optional if not present.
     */
    static Optional<ScoreboardSnapshot> take() {
        return GPSUtilImpl.getScoreboard();
    }

    /**
     * <p>Get the displayed title of the scoreboard, at the top.</p>
     *
     * <p>Example: <code>snapshot.title() = "LARGESERVER.COM"</code></p>
     *
     * @return The displayed title.
     */
    Component title();

    /**
     * <p>A list of all entries displayed on the scoreboard. The first value is the name (on the left), and the second is
     * the value (on the right).</p>
     *
     * <p>Example: <code>snapshot.entries() = [("Autumn 12th", "7"), ("9:20am", "6"), ...]</code></p>
     *
     * @return A list of all currently displayed entries.
     */
    List<Pair<Component, Component>> entries();

    /**
     * <p>Return an optional containing the entry on the given row, counting top to bottom. If out of range, then an empty
     * optional is returned. Does not count the title.</p>
     *
     * <p>Example: <code>snapshot.entryFromTop(1) = Optional[("9:20am", "6")]</code></p>
     *
     * @param rowsFromTop How many rows from the top to look for.
     * @return An optional containing the entry at the given index, or an empty optional otherwise.
     */
    Optional<Pair<Component, Component>> entryFromTop(int rowsFromTop);

    /**
     * <p>Return an optional containing the entry on the given row, counting bottom to top. If out of range, then an empty
     * optional is returned. Does not count the title.</p>
     *
     * <p>Example: <code>snapshot.entryFromBottom(2) = Optional[("Balance: 400", "3")]</code></p>
     *
     * @param rowsFromBottom How many rows from the bottom to look for.
     * @return An optional containing the entry at the given index, or an empty optional otherwise.
     */
    Optional<Pair<Component, Component>> entryFromBottom(int rowsFromBottom);

    /**
     * <p>Return an optional possibly containing the first found entry whose name is prefixed with the given string.</p>
     *
     * <p>Example: <code>snapshot.entryWithPrefix("Area") = Optional[("Area: Mines", "4")]</code></p>
     *
     * @param prefix Prefix to search for.
     * @return An optional containing an entry with the name matching the given prefix, or an empty optional otherwise.
     */
    Optional<Pair<Component, Component>> entryWithNamePrefix(String prefix);

    /**
     * <p>A list of all names displayed on the scoreboard, on the left side.</p>
     *
     * <p>Example: <code>snapshot.names() = ["Autumn 12th", "9:20am", "", ...]</code></p>
     *
     * @return A list of all currently displayed entry names.
     */
    List<Component> names();

    /**
     * <p>Return an optional possibly containing the first found name which is prefixed with the given string.</p>
     *
     * <p>Example: <code>snapshot.nameWithPrefix("Balance: ") = Optional["Balance: 400"]</code></p>
     *
     * @param prefix Prefix to search for.
     * @return An optional containing the given name with the prefix, or an empty optional otherwise.
     */
    Optional<Component> nameWithPrefix(String prefix);

    /**
     * <p>Return an optional possibly containing the first found name which is prefixed with the given string. This version
     * returns it in String format, <b>with the given prefix removed</b>.</p>
     *
     * <p>Example: <code>snapshot.nameWithPrefixStripped("Area: ") = Optional["Mines"]</code></p>
     *
     * @param prefix Prefix to search for.
     * @return An optional containing the given name with the prefix, with said prefix removed, or an empty optional otherwise.
     */
    Optional<String> nameWithPrefixStripped(String prefix);

    /**
     * A list of all names displayed on the scoreboard, on the left side.
     *
     * @return A list of all currently displayed entry values.
     */
    List<Component> values();
}

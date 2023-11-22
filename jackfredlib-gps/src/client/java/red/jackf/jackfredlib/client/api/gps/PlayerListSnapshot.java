package red.jackf.jackfredlib.client.api.gps;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.client.impl.gps.GPSUtilImpl;

import java.util.List;
import java.util.Optional;

/**
 * <p>Represents a snapshot of the current player list, as shown to the client. The player does not need to be displaying
 * the player list for these methods to work.</p>
 *
 * @see Component#getString()
 */
@ApiStatus.AvailableSince("1.0.7")
public interface PlayerListSnapshot {
    /**
     * Take a snapshot of the current player list.
     *
     * @return A snapshot of the player list at the given time.
     */
    static PlayerListSnapshot take() {
        return GPSUtilImpl.getPlayerList();
    }

    /**
     * Returns an optional possibly containing the header, shown above the list of players.
     *
     * @return An optional containing the header above the list of players, or an empty optional if not present.
     */
    Optional<Component> header();

    /**
     * Returns an optional possibly containing the footer, shown below the list of players.
     *
     * @return An optional containing the footer below the list of players, or an empty optional if not present.
     */
    Optional<Component> footer();

    /**
     * Returns the dimensions of the player list.
     *
     * @return The width and height of the player list.
     */
    Dimensions size();

    /**
     * Returns a list of all currently displayed names on the player list.
     *
     * @return A list of all currently displayed players on the list.
     */
    List<Component> names();

    /**
     * Returns an optional possibly containing a name prefixed with the given prefix.
     *
     * @param prefix Prefix to search with.
     * @return An optional containing a player name with the given prefix, or an empty optional otherwise.
     */
    Optional<Component> nameWithPrefix(String prefix);

    /**
     * <p>Returns an optional possibly containing a name prefixed with the given prefix. This version returns it as a String,
     * <b>with the given prefix removed</b>.</p>
     *
     * @param prefix Prefix to search with and remove after.
     * @return An optional containing a player name with the given prefix, with said prefix removed, or an empty optional otherwise.
     */
    Optional<String> nameWithPrefixStripped(String prefix);

    /**
     * Returns an optional possibly containing the player name at the given column and row. If the column or row is out
     * of bounds, then an empty optional is returned.
     *
     * @see Dimensions
     *
     * @param column Column to look in, from left to right.
     * @param row Row to look in, from top to bottom.
     * @return The player name at the given row or column, or an empty optional if out of bounds.
     */
    Optional<Component> nameAtPosition(int column, int row);

    /**
     * <p>The dimensions of a given snapshot of the player list.</p>
     *
     * <p>These are calculated based on the number of players displayed on the list, real or fake. For X players:</p>
     * <ul>
     *     <li>1-20: 1 columns, <code>X</code> rows</li>
     *     <li>21-40: 2 columns, <code>ceil(X / 2)</code> rows</li>
     *     <li>41-60: 3 columns, <code>ceil(X / 3)</code> rows</li>
     *     <li>61-80: 4 columns, ceil(X / 4) rows</li>
     * </ul>
     * <p>Any players after 80 will not be shown.</p>
     *
     * @param columns How many columns the player list has.
     * @param rows
     */
    record Dimensions(int columns, int rows) {}
}

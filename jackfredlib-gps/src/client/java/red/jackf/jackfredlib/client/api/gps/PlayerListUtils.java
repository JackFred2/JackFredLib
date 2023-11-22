package red.jackf.jackfredlib.client.api.gps;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

/**
 * <p>Utilities for working with the in-game player list (when holding tab). Many large servers use these to show details
 * other than players, such as Hypixel Skyblock's current location.</p>
 *
 * @deprecated Use {@link PlayerListSnapshot#take()} and the given methods.
 */
@Deprecated(since = "1.0.7", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "1.1")
public class PlayerListUtils {
    /**
     * Return the header of the player list if one exists, an empty optional if not present
     * @return An Optional containing the current header for the in-game player list, or an empty Optional if not present.
     */
    public static Optional<String> getHeader() {
        return PlayerListSnapshot.take().header().map(Component::getString);
    }

    /**
     * Return the footer of the player list if one exists, an empty optional if not present
     * @return An Optional containing the current footer for the in-game player list, or an empty Optional if not present.
     */
    public static Optional<String> getFooter() {
        return PlayerListSnapshot.take().footer().map(Component::getString);
    }

    /**
     * Return a list of all tab list entries as strings.
     *
     * @return A list of all tab list entries, mapped to a string.
     */
    public static List<String> getAll() {
        return PlayerListSnapshot.take().names().stream()
                .map(Component::getString)
                .toList();
    }

    /**
     * Returns the contents of a player list entry which is prefixed with a string, with said prefix removed, or an empty
     * optional otherwise.
     *
     * @param prefix Prefix to filter by and strip.
     * @return An Optional containing a string which was prefixed by <code>prefix</code>, or an empty optional if not
     * found.
     */
    public static Optional<String> getPrefixed(String prefix) {
        return PlayerListSnapshot.take().nameWithPrefixStripped(prefix);
    }

    /**
     * Get the number of columns and rows of the player list, in entries.
     *
     * @return A Dimensions marking the current size of the player list widget.
     */
    public static Dimensions getWidgetSize() {
        PlayerListSnapshot.Dimensions oldDimensions = PlayerListSnapshot.take().size();
        return new Dimensions(oldDimensions.columns(), oldDimensions.rows());
    }

    /**
     * Get the entry from a given row and column off the player list.
     *
     * @param column Column to grab the entry from. 0-indexed from left to right, or can be negative to count from right
     *               to left. For example, <code>0</code> takes from the left-most column, while <code>-1</code> takes
     *               from the right-most.
     * @param row    Column to grab the entry from. 0-indexed from top to bottom, or can be negative to count from
     *               bottom to top. For example, <code>0</code> takes from the top row, while <code>-1</code> takes
     *               from the bottom.
     * @return An Optional containing the entry at a given position, or and empty optional if out of bounds / not present.
     */
    public static Optional<String> getFromPosition(int column, int row) {
        return PlayerListSnapshot.take().nameAtPosition(column, row).map(Component::getString);
    }

    /**
     * A count of columns and rows of the player list.
     * @param columns How many columns the player list has, between 1 and 4.
     * @param rows How many rows the player list has, between 1 and 20.
     */
    public record Dimensions(int columns, int rows) {}
}

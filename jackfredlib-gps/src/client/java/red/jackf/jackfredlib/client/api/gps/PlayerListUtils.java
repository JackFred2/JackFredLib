package red.jackf.jackfredlib.client.api.gps;

import red.jackf.jackfredlib.client.impl.gps.GPSUtilImpl;

import java.util.List;
import java.util.Optional;

/**
 * Utilities for working with the in-game player list (when holding tab). Many large servers use these to show details
 * other than players, such as Hypixel Skyblock's current location.
 */
public class PlayerListUtils {
    /**
     * Return the header of the player list if one exists, an empty optional if not present
     * @return An Optional containing the current header for the in-game player list, or an empty Optional if not present.
     */
    public static Optional<String> getHeader() {
        return Optional.ofNullable(GPSUtilImpl.getPlayerListHeader());
    }

    /**
     * Return the footer of the player list if one exists, an empty optional if not present
     * @return An Optional containing the current footer for the in-game player list, or an empty Optional if not present.
     */
    public static Optional<String> getFooter() {
        return Optional.ofNullable(GPSUtilImpl.getPlayerListFooter());
    }

    /**
     * Return a list of all tab list entries as strings.
     *
     * @return A list of all tab list entries, mapped to a string.
     */
    public static List<String> getAll() {
        return GPSUtilImpl.getPlayerList();
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
        return GPSUtilImpl.getPlayerList().stream()
                .filter(s -> s.startsWith(prefix))
                .findFirst()
                .map(s -> s.substring(prefix.length()));
    }

    /**
     * Get the number of columns and rows of the player list, in entries.
     *
     * @return A Dimensions marking the current size of the player list widget.
     */
    public static Dimensions getWidgetSize() {
        int listSize = GPSUtilImpl.getPlayerList().size();
        int rows = listSize;
        int columns;
        for (columns = 1; rows > 20; rows = (listSize + columns - 1) / columns) {
            ++columns;
        }
        return new Dimensions(columns, rows);
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
        List<String> list = GPSUtilImpl.getPlayerList();
        var dimensions = getWidgetSize();
        int targetedColumn = column < 0 ? dimensions.columns + column : column;
        int targetedRow = row < 0 ? dimensions.rows + row : row;
        if (targetedColumn < 0 || targetedColumn >= dimensions.columns || targetedRow < 0 || targetedRow >= dimensions.rows)
            return Optional.empty();
        int index = targetedRow * dimensions.columns + targetedColumn;
        if (index < list.size()) return Optional.of(list.get(index));
        return Optional.empty();
    }

    /**
     * A count of columns and rows of the player list.
     * @param columns How many columns the player list has, between 1 and 4.
     * @param rows How many rows the player list has, between 1 and 20.
     */
    public record Dimensions(int columns, int rows) {}
}

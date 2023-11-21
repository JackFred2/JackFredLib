package red.jackf.jackfredlib.client.api.gps;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.client.impl.gps.GPSUtilImpl;

import java.util.List;
import java.util.Optional;

/**
 * Represents a snapshot of the right-hand scoreboard currently displayed to the client.
 */
public interface ScoreboardSnapshot {
    /**
     * Get a current snapshot of the player scoreboard, on the right hand of the screen. Returns an empty optional if
     * the scoreboard is not currently displayed.
     *
     * @return An optional containing a snapshot of the current scoreboard, or an empty optional if not present.
     */
    static Optional<ScoreboardSnapshot> getCurrent() {
        return GPSUtilImpl.getScoreboard();
    }

    /**
     * Get the displayed title of the scoreboard, at the top.
     *
     * @return The displayed title.
     */
    Component title();

    /**
     * A list of all entries displayed on the scoreboard. The first value is the name (on the left), and the second is
     * the value (on the right).
     *
     * @return A list of all currently displayed entries.
     */
    List<Pair<Component, Component>> entries();

    /**
     * A list of all names displayed on the scoreboard, on the left side.
     *
     * @return A list of all currently displayed entry names.
     */
    List<Component> names();

    /**
     * A list of all names displayed on the scoreboard, on the left side.
     *
     * @return A list of all currently displayed entry values.
     */
    List<Component> values();
}

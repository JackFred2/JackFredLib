package red.jackf.jackfredlib.api.lying;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.impl.lying.tracker.TrackerBuilderImpl;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>Trackers allow you to link one or more lies with an area of space, and automatically add or remove players who enter
 * said area to the given lies.</p>
 *
 * <p>The position being tracked is the player's feet ({@link ServerPlayer#getPosition(float)}). The areas that trackers
 * can function for are:</p>
 *
 * <ul>
 *     <li>A point in space along with a radius</li>
 *     <li>A bounding box in the world</li>
 *     <li>Planned: Tracking an entity</li>
 * </ul>
 *
 * <p>Faded lies will automatically be removed from the tracker. Additionally, if no lies are left being managed by this
 * tracker then it will shut down. This behavior can be overridden, but be aware of leaking trackers.</p>
 */
public interface Tracker<L extends Lie> {
    /**
     * Create a new builder for a tracker.
     *
     * @param level Level that the tracker will check for players in.
     * @return A new builder for a tracker.
     * @param <L> Superclass of all lies being tracked.
     */
    static <L extends Lie> TrackerBuilder<L> builder(@NotNull ServerLevel level) {
        return new TrackerBuilderImpl<>(level);
    }

    /**
     * Whether this tracker is currently running.
     *
     * @return Whether this tracker is currently running.
     */
    boolean isRunning();

    /**
     * Start or stop this tracker.
     *
     * @param run Whether this tracker should run.
     */
    void setRunning(boolean run);

    /**
     * Return all lies currently managed by this tracker.
     *
     * @return Unmodifiable list of all lies currently being managed by this tracker.
     */
    Collection<L> getManagedLies();

    /**
     * Add a lie to this tracker's set of managed lies.
     *
     * @param lie Lie to add to this tracker.
     * @return Whether the lie was added to this tracker; false if already tracked.
     */
    boolean addLie(@NotNull L lie);

    /**
     * Remove a lie from this tracker's set of managed lies.
     *
     * @param lie Lie to remove from this tracker.
     * @return Whether the lie was removed from this tracker; false if not already tracked.
     */
    boolean removeLie(@NotNull L lie);

    /**
     * Sets this tracker's requirement to be within a sphere focused around a point in space.
     *
     * @param focus Center of the spherical area that players need to be in to see the lie.
     * @param radius Distance around the point that players need to be in to see the lie.
     */
    void setFocus(@NotNull Vec3 focus, double radius);

    /**
     * Sets this tracker's requirement to be within a bounding box.
     *
     * @param bounds Bounding box that the player needs to be within to see the lie.
     */
    void setBounds(@NotNull AABB bounds);

    /**
     * A builder class for creating a tracker.
     *
     * @param <L> Superclass of all lies managed by this tracker.
     */
    interface TrackerBuilder<L extends Lie> {

        /**
         * Add a lie to be managed by this tracker.
         *
         * @param lie Lie to be managed by this tracker.
         */
        TrackerBuilder<L> addLie(@NotNull L lie);

        /**
         * Add a collection of lies to be managed by this tracker.
         *
         * @param lies Collection of lies to be managed by this tracker.
         */
        TrackerBuilder<L> addLies(@NotNull Collection<L> lies);

        /**
         * Add a stream of lies to be managed by this tracker.
         *
         * @param lies Stream of lies to be managed by this tracker.
         */
        TrackerBuilder<L> addLies(@NotNull Stream<L> lies);

        /**
         * Add an array of lies to be managed by this tracker.
         *
         * @param lies Array of lies to be managed by this tracker.
         */
        TrackerBuilder<L> addLies(@NotNull L[] lies);

        /**
         * Sets the position requirement to be within a spherical area around a point.
         *
         * @param focus Center of the spherical area that players need to be in to see the lie.
         * @param radius Distance around the point that players need to be in to see the lie.
         */
        TrackerBuilder<L> setFocus(@NotNull Vec3 focus, double radius);

        /**
         * Sets the position requirement to be within a bounding box.
         *
         * @param bounds Bounding box that the player needs to be within to see the lie.
         */
        TrackerBuilder<L> setBounds(@NotNull AABB bounds);

        /**
         * Sets the update interval for this tracker. Players will only be added or removed from lies every
         * <code>updateInterval</code> ticks.
         *
         * @param updateInterval Number of ticks between each update.
         * @apiNote Default interval is 20 ticks (1 second).
         */
        TrackerBuilder<L> setUpdateInterval(long updateInterval);

        /**
         * Set the predicate for a player to see this tracker's managed lies. By default, any player within range can
         * see the lies. All predicates added by this method are combined and required of the given player.
         *
         * @param playerPredicate Requirement for players to see this tracker's lies.
         */
        TrackerBuilder<L> addPredicate(@NotNull Predicate<ServerPlayer> playerPredicate);

        /**
         * By default, when all of a tracker's lies have faded, the tracker will remove itself from ticking. This can
         * be disabled with this method, however be careful not to forget and leak trackers.
         */
        TrackerBuilder<L> keepWhenEmpty();

        /**
         * Create a tracker from this given arguments.
         *
         * @param startRunning Whether this tracker should be running when created.
         * @return The constructed tracker.
         * @throws NullPointerException If a position requirement is not given
         */
        Tracker<L> build(boolean startRunning);
    }
}

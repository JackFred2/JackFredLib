package red.jackf.jackfredlib.api.lying;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Helper methods for creating simple positional predicates for {@link Tracker} objects.
 */
public interface TrackerPredicates {
    /**
     * Creates a predicate to be within a spherical area around a point.
     *
     * @param focus Center of the spherical area that players need to be in to see the lie.
     * @param radius Distance around the point that players need to be in to see the lie.
     */
    static Predicate<Vec3> forFocus(Vec3 focus, double radius) {
        final double radiusSqr = radius * radius;

        return pos -> pos.distanceToSqr(focus) < radiusSqr;
    }

    /**
     * Creates a predicate to be within a bounding box.
     *
     * @param bounds Bounding box that the player needs to be within to see the lie.
     */
    static Predicate<Vec3> forBounds(AABB bounds) {
        return bounds::contains;
    }

    /**
     * Creates a predicate to be within a radius around a given entity. If the entity is removed, the
     * predicate always fails.
     *
     * @param focus Focal entity to look around.
     * @param radius Radius around said entity.
     */
    static Predicate<Vec3> forEntity(@NotNull Entity focus, double radius) {
        final double radiusSqr = radius * radius;

        return pos -> {
            if (focus.isRemoved()) return false;
            return pos.distanceToSqr(focus.position()) < radiusSqr;
        };
    }
}

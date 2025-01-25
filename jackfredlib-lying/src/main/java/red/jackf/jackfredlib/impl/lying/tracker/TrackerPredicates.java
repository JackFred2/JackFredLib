package red.jackf.jackfredlib.impl.lying.tracker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface TrackerPredicates {
    static Predicate<Vec3> forFocus(Vec3 focus, double radius) {
        final double radiusSqr = radius * radius;

        return pos -> pos.distanceToSqr(focus) < radiusSqr;
    }

    static Predicate<Vec3> forBounds(AABB bounds) {
        return bounds::contains;
    }

    static Predicate<Vec3> forEntity(@NotNull Entity focus, double radius) {
        final double radiusSqr = radius * radius;

        return pos -> pos.distanceToSqr(focus.position()) < radiusSqr;
    }
}

package red.jackf.jackfredlib.impl.lying.tracker;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public interface TrackerPredicates {
    static Predicate<Vec3> forFocus(Vec3 focus, double radius) {
        final double radiusSqr = radius * radius;

        return pos -> pos.distanceToSqr(focus) < radiusSqr;
    }

    static Predicate<Vec3> forBounds(AABB bounds) {
        return bounds::contains;
    }
}

package red.jackf.jackfredlib.impl.lying.tracker;

import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.api.lying.Tracker;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TrackerBuilderImpl<L extends Lie> implements Tracker.TrackerBuilder<L> {
    private final ServerLevel level;
    private final Set<L> startLies = new HashSet<>();
    private Predicate<Vec3> positionPredicate = null;
    private long updateInterval = SharedConstants.TICKS_PER_SECOND;
    private Predicate<ServerPlayer> playerPredicate = ignored -> true;
    private boolean keepWhenEmpty = false;

    public TrackerBuilderImpl(ServerLevel level) {
        this.level = level;
    }

    @Override
    public TrackerBuilderImpl<L> addLie(@NotNull L lie) {
        this.startLies.add(lie);
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> addLies(@NotNull Collection<L> lies) {
        this.startLies.addAll(lies);
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> addLies(@NotNull Stream<L> lies) {
        lies.forEach(this.startLies::add);
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> addLies(L[] lies) {
        this.startLies.addAll(Arrays.asList(lies));
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> setFocus(@NotNull Vec3 focus, double radius) {
        this.positionPredicate = TrackerPredicates.forFocus(focus, radius);
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> setBounds(@NotNull AABB bounds) {
        this.positionPredicate = TrackerPredicates.forBounds(bounds);
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> addPredicate(@NotNull Predicate<ServerPlayer> additionalPredicate) {
        if (this.playerPredicate == null) this.playerPredicate = additionalPredicate;
        else this.playerPredicate = this.playerPredicate.and(additionalPredicate);
        return this;
    }

    @Override
    public TrackerBuilderImpl<L> keepWhenEmpty() {
        this.keepWhenEmpty = true;
        return this;
    }

    @Override
    public Tracker<L> build(boolean startRunning) {
        Objects.requireNonNull(positionPredicate, "A position predicate is required for a tracker");
        if (updateInterval <= 0) throw new IllegalArgumentException("Update rate must be at least 1");
        TrackerImpl<L> built = new TrackerImpl<>(level, positionPredicate, updateInterval, playerPredicate, keepWhenEmpty, startLies);
        if (startRunning) built.setRunning(true);
        return built;
    }
}

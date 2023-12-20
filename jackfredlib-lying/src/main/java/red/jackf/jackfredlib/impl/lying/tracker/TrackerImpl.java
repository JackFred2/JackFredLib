package red.jackf.jackfredlib.impl.lying.tracker;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.api.lying.Tracker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TrackerImpl<L extends Lie> implements Tracker<L> {
    private final ServerLevel level;
    private Predicate<Vec3> positionPredicate;
    private final long updateInterval;
    private final Predicate<ServerPlayer> playerPredicate;
    private final boolean keepWhenEmpty;
    private final Set<L> lies;

    private long startTick = -1;
    private boolean running = false;

    public TrackerImpl(ServerLevel level,
                       Predicate<Vec3> positionPredicate,
                       long updateInterval,
                       Predicate<ServerPlayer> playerPredicate,
                       boolean keepWhenEmpty,
                       Collection<L> lies) {
        this.level = level;
        this.positionPredicate = positionPredicate;
        this.updateInterval = updateInterval;
        this.playerPredicate = playerPredicate;
        this.keepWhenEmpty = keepWhenEmpty;
        this.lies = new HashSet<>(lies);
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void setRunning(boolean run) {
        if (this.running != run) {
            this.running = run;

            if (run) {
                this.startTick = this.level.getGameTime();
                TrackerRunner.addTracker(this.level, this);
            } else {
                TrackerRunner.removeTracker(this.level, this);
            }
        }
    }

    public void tick() {
        if ((this.level.getGameTime() - this.startTick) % this.updateInterval == 0) {
            this.lies.removeIf(Lie::hasFaded);

            if (this.lies.isEmpty() && !this.keepWhenEmpty) {
                TrackerRunner.removeTracker(this.level, this);
                return;
            }

            var shouldView = new HashSet<ServerPlayer>();

            for (ServerPlayer player : PlayerLookup.world(this.level)) {
                if (!this.playerPredicate.test(player)) continue;
                if (!this.positionPredicate.test(player.getPosition(1))) continue;
                shouldView.add(player);
            }

            for (L lie : this.lies) {
                for (ServerPlayer viewer : lie.getViewingPlayers()) {
                    if (!shouldView.contains(viewer)) {
                        lie.removePlayer(viewer);
                    }
                }

                var currentlyViewing = lie.getViewingPlayers();

                for (ServerPlayer newViewer : shouldView) {
                    if (!currentlyViewing.contains(newViewer)) {
                        lie.addPlayer(newViewer);
                    }
                }
            }
        }
    }

    @Override
    public Collection<L> getManagedLies() {
        return ImmutableList.copyOf(this.lies);
    }

    @Override
    public boolean addLie(@NotNull L lie) {
        return this.lies.add(lie);
    }

    @Override
    public boolean removeLie(@NotNull L lie) {
        return this.lies.remove(lie);
    }

    @Override
    public void setFocus(@NotNull Vec3 focus, double radius) {
        this.positionPredicate = TrackerPredicates.forFocus(focus, radius);
    }

    @Override
    public void setBounds(@NotNull AABB bounds) {
        this.positionPredicate = TrackerPredicates.forBounds(bounds);
    }
}

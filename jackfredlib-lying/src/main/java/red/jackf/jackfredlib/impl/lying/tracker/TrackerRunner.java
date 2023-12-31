package red.jackf.jackfredlib.impl.lying.tracker;

import net.minecraft.server.level.ServerLevel;

import java.util.*;

public class TrackerRunner {
    public static final Map<ServerLevel, TrackerRunner> RUNNERS = new HashMap<>();
    private final ServerLevel level;
    private final Set<TrackerImpl<?>> trackers = new HashSet<>();

    public TrackerRunner(ServerLevel level) {
        this.level = level;
    }

    private void tickAll() {
        for (TrackerImpl<?> tracker : List.copyOf(this.trackers)) {
            if (tracker.isRunning()) {
                tracker.tick();
            } else {
                // shouldn't happen but just in case
                removeTracker(this.level, tracker);
            }
        }
    }

    public static void addTracker(ServerLevel level, TrackerImpl<?> tracker) {
        var runner = RUNNERS.get(level);
        if (runner == null) return;
        runner.trackers.add(tracker);
        tracker.tick();
    }

    public static void removeTracker(ServerLevel level, TrackerImpl<?> tracker) {
        var runner = RUNNERS.get(level);
        if (runner == null) return;
        runner.trackers.remove(tracker);
    }

    public static void loadLevel(ServerLevel level) {
        RUNNERS.put(level, new TrackerRunner(level));
    }

    public static void unloadLevel(ServerLevel level) {
        RUNNERS.remove(level);
    }

    public static void tickLevel(ServerLevel level) {
        TrackerRunner runner = RUNNERS.get(level);
        if (runner == null) return;
        runner.tickAll();
    }
}

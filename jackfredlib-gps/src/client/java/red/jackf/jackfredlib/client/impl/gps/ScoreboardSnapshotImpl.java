package red.jackf.jackfredlib.client.impl.gps;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.api.base.Memoizer;
import red.jackf.jackfredlib.client.api.gps.ScoreboardSnapshot;

import java.util.List;

/**
 * Represents a snapshot of the right-hand scoreboard currently displayed to the client.
 */
public final class ScoreboardSnapshotImpl implements ScoreboardSnapshot {
    private final Component title;
    private final List<Pair<Component, Component>> entries;
    private final Memoizer<List<Component>> names;
    private final Memoizer<List<Component>> values;

    public ScoreboardSnapshotImpl(Component title, List<Pair<Component, Component>> entries) {
        this.title = title;
        this.entries = entries;
        this.names = Memoizer.of(() -> entries.stream().map(Pair::getFirst).toList());
        this.values = Memoizer.of(() -> entries.stream().map(Pair::getSecond).toList());
    }

    public Component title() {
        return title;
    }

    public List<Pair<Component, Component>> entries() {
        return entries;
    }

    public List<Component> names() {
        return names.get();
    }

    public List<Component> values() {
        return values.get();
    }
}

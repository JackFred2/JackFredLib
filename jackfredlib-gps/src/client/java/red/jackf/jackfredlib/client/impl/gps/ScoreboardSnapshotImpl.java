package red.jackf.jackfredlib.client.impl.gps;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.api.base.Memoizer;
import red.jackf.jackfredlib.client.api.gps.ScoreboardSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public Optional<Pair<Component, Component>> entryFromTop(int rowsFromTop) {
        if (rowsFromTop < 0 || rowsFromTop >= entries.size()) return Optional.empty();
        return Optional.of(entries.get(rowsFromTop));
    }

    @Override
    public Optional<Pair<Component, Component>> entryFromBottom(int rowsFromBottom) {
        if (rowsFromBottom < 0 || rowsFromBottom >= entries.size()) return Optional.empty();
        return Optional.of(entries.get(entries.size() - rowsFromBottom - 1));
    }

    @Override
    public Optional<Pair<Component, Component>> entryWithNamePrefix(String prefix) {
        return entries.stream()
                      .filter(pair -> pair.getFirst().getString().startsWith(prefix))
                      .findFirst();
    }

    public List<Component> names() {
        return names.get();
    }

    @Override
    public Optional<Component> nameWithPrefix(String prefix) {
        return entryWithNamePrefix(prefix)
                .map(Pair::getFirst);
    }

    @Override
    public Optional<String> nameWithPrefixStripped(String prefix) {
        return nameWithPrefix(prefix).map(component -> component.getString().substring(prefix.length()));
    }

    public List<Component> values() {
        return values.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreboardSnapshotImpl that = (ScoreboardSnapshotImpl) o;
        return Objects.equals(title, that.title) && Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, entries);
    }

    @Override
    public String toString() {
        return "ScoreboardSnapshotImpl{" +
                "title=" + title +
                ", entries=" + entries +
                '}';
    }
}

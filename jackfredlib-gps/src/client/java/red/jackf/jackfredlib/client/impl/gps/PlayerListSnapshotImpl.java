package red.jackf.jackfredlib.client.impl.gps;

import net.minecraft.network.chat.Component;
import red.jackf.jackfredlib.client.api.gps.PlayerListSnapshot;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record PlayerListSnapshotImpl(Optional<Component> header,
                                     Optional<Component> footer,
                                     List<Component> names) implements PlayerListSnapshot {
    @Override
    public Dimensions size() {
        int rows = names.size();
        int columns;
        for (columns = 1; rows > 20; rows = (names.size() + columns - 1) / columns) {
            ++columns;
        }

        return new Dimensions(columns, rows);
    }

    @Override
    public Optional<Component> nameWithPrefix(String prefix) {
        return names.stream()
                    .filter(component -> component.getString().startsWith(prefix))
                    .findFirst();
    }

    @Override
    public Optional<String> nameWithPrefixStripped(String prefix) {
        return nameWithPrefix(prefix).map(component -> component.getString().substring(prefix.length()));
    }

    @Override
    public Optional<Component> nameAtPosition(int column, int row) {
        Dimensions size = size();
        if (column < 0 || row < 0 || column >= size.columns() || row >= size.rows()) return Optional.empty();
        int index = row * size.columns() + column;
        if (index >= names.size()) return Optional.empty();
        return Optional.of(names.get(index));
    }

    @Override
    public String toString() {
        return "PlayerListSnapshotImpl{" +
                "header=" + header +
                ", footer=" + footer +
                ", names=" + names +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerListSnapshotImpl that = (PlayerListSnapshotImpl) o;
        return Objects.equals(header, that.header) && Objects.equals(footer, that.footer) && Objects.equals(names, that.names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, footer, names);
    }
}

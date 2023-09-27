package red.jackf.jackfredlib.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class CoordinateUtils {


    /**
     * Returns a free path for a given coordinate, when treating the ID as a file path. Slashes are treated as folders
     * to descend into. This method does not create parent directories; consider using
     * {@link Files#createDirectories(Path, FileAttribute[])}.
     *
     * @param coordinate Coordinate to derive a free path from
     * @param base Base path to look for a free path relative to
     * @param suffix A suffix or file extension to append when looking for a free path.
     * @return A path approximately matching the given coordinate's ID that does not already exist.
     */
    public static Path getNextFreeFilePath(Coordinate coordinate, Path base, String suffix) {
        // TODO implement
        return base;
    }
}

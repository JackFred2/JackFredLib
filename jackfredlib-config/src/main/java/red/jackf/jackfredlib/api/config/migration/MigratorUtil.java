package red.jackf.jackfredlib.api.config.migration;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

/**
 * Utilities for working with JFLib-Config's Migration system.
 */
public class MigratorUtil {
    /**
     * Tries to parse a String into a Version, or throws a Runtime exception if not possible. You most likely won't need
     * this.
     * @param versionStr Version string to try and parse
     * @return The parsed version string
     * @throws RuntimeException If the string can't be parsed into a version.
     */
    public static Version unsafeParse(String versionStr) {
        try {
            return Version.parse(versionStr);
        } catch (VersionParsingException ex) {
            throw new RuntimeException(ex);
        }
    }
}

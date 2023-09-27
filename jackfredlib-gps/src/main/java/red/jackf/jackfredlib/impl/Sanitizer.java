package red.jackf.jackfredlib.impl;

import net.minecraft.SharedConstants;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class Sanitizer {
    private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", Pattern.CASE_INSENSITIVE);

    /**
     * Sanitizes a string so as to make it safe for a file name. Adjusted from {@link net.minecraft.FileUtil#findAvailableName(Path, String, String)}
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        char[] var3 = SharedConstants.ILLEGAL_FILE_CHARACTERS;

        for (char c : var3) {
            input = input.replace(c, '_');
        }

        input = input.replaceAll("[./\"]", "_");
        if (RESERVED_WINDOWS_FILENAMES.matcher(input).matches()) {
            input = "_" + input + "_";
        }

        return input;
    }
}

package red.jackf.jackfredlib.impl.colour;

import net.minecraft.world.item.DyeColor;
import red.jackf.jackfredlib.api.colour.Colour;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ColourUtils {
    private static final Map<DyeColor, Colour> COLOUR_MAP = Arrays.stream(DyeColor.values())
            .collect(Collectors.toMap(c -> c, c -> Colour.fromInt(c.getFireworkColor())));

    private static double distance(Colour a, Colour b) {
        final double factor = 2.0;
        return Math.pow(Math.abs(b.r() - a.r()), factor)
                + Math.pow(Math.abs(b.g() - a.g()), factor)
                + Math.pow(Math.abs(b.b() - a.b()), factor);
    }

    public static DyeColor closestDyeTo(Colour colour) {
        DyeColor lowest = DyeColor.WHITE;
        double lowestDistance = Double.POSITIVE_INFINITY;

        for (Map.Entry<DyeColor, Colour> entry : COLOUR_MAP.entrySet()) {
            double distance = distance(entry.getValue(), colour);
            if (distance < lowestDistance) {
                lowest = entry.getKey();
                lowestDistance = distance;
            }
        }

        return lowest;
    }
}

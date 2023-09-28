package red.jackf.jackfredlib.client.impl.toasts;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.client.api.toasts.ImageSpec;
import red.jackf.jackfredlib.impl.base.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModUtils {
    private static final Map<String, ImageSpec> CACHE = new HashMap<>();
    private static final Logger LOGGER = LogUtil.getLogger("Toasts/ModIconLoader");
    private ModUtils() {}

    public static Component getModTitle(String modid) {
        return FabricLoader.getInstance().getModContainer(modid)
                .map(container -> Component.literal(container.getMetadata().getName()))
                .orElse(Component.literal(modid));
    }

    public static @Nullable ImageSpec specFromModId(String modid) {
        return CACHE.computeIfAbsent(modid, ModUtils::_specFromModId);
    }

    private static void devOnlyWarn(String str, Object... args) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment())
            LOGGER.warn(str, args);
    }

    private static @Nullable ImageSpec _specFromModId(String modid) {
        LOGGER.debug("Trying to load icon for mod '{}'", modid);
        var mod = FabricLoader.getInstance().getModContainer(modid).orElse(null);
        if (mod == null) {
            devOnlyWarn("Mod '{}' was not found.", modid);
            return null;
        }
        var iconPath = mod.getMetadata().getIconPath(CustomToastImpl.IMAGE_SIZE).orElse(null);
        if (iconPath == null) {
            devOnlyWarn("No icon path found for mod '{}'", modid);
            return null;
        }

        var absPath = mod.findPath(iconPath).orElse(null);
        if (absPath == null) {
            devOnlyWarn("Couldn't get absolute icon path for mod '{}'", modid);
            return null;
        }

        try (InputStream inputStream = Files.newInputStream(absPath)) {
            NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));

            // trim assets/{modid}/; shouldn't cause issues but if it does we'll return null
            var trimmedPath = iconPath.replace("assets/%s/".formatted(modid), "");

            var spec = ImageSpec.image(new ResourceLocation(modid, trimmedPath), image.getWidth(), image.getHeight());
            LOGGER.debug("Caching spec {}:{} ({}x{})", modid, iconPath, image.getWidth(), image.getHeight());
            return spec;
        } catch (IOException ex) {
            LOGGER.error("IO error loading icon for mod '{}': ", modid, ex);
            return null;
        }
    }
}

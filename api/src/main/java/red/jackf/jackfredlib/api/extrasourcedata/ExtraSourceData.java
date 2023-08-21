package red.jackf.jackfredlib.api.extrasourcedata;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface ExtraSourceData<T extends ExtraSourceData<T>> {
    T copy();

    record Definition<T>(ResourceLocation id, Class<T> clazz, Supplier<T> factory) { }
}

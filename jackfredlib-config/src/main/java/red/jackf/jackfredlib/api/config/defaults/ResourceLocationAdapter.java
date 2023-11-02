package red.jackf.jackfredlib.api.config.defaults;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.Marshaller;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationAdapter {
    public static ResourceLocation deserializer(JsonPrimitive primitive, Marshaller marshaller) throws DeserializationException {
        if (primitive.getValue() instanceof String str) {
            ResourceLocation result = ResourceLocation.tryParse(str);
            if (result == null) throw new DeserializationException("Invalid Resource Location: " + str);
            return result;
        } else {
            throw new DeserializationException("Resource Location cannot be deserialized from a non-string");
        }
    }

    public static JsonElement serializer(ResourceLocation location, Marshaller marshaller) {
        return new JsonPrimitive(location.toString());
    }
}

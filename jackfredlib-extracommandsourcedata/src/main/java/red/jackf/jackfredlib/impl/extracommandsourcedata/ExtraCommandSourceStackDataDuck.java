package red.jackf.jackfredlib.impl.extracommandsourcedata;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import net.minecraft.resources.ResourceLocation;
import red.jackf.jackfredlib.api.extracommandsourcedata.ExtraSourceData;

import java.util.Map;

public interface ExtraCommandSourceStackDataDuck {
    Dynamic2CommandExceptionType INVALID_DATA_TYPE = new Dynamic2CommandExceptionType((expected, actual) ->
            new LiteralMessage("Invalid extra data class requested; expected %s but got %s".formatted(expected, actual)));

    void jackfredlib$setData(Map<ResourceLocation, ExtraSourceData<?>> data);

    <T extends ExtraSourceData<T>> T jackfredlib$getData(ExtraSourceData.Definition<T> definition) throws CommandSyntaxException;
}

package red.jackf.jackfredlib.mixins.extrasourcedata;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.jackfredlib.api.extrasourcedata.ExtraSourceData;
import red.jackf.jackfredlib.impl.extrasourcedata.JFLibCommandSourceStackDuck;

import java.util.HashMap;
import java.util.Map;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin implements JFLibCommandSourceStackDuck {
    @Unique
    private final Map<ResourceLocation, ExtraSourceData<?>> extraData = new HashMap<>();

    @ModifyReturnValue(method = {
            "withSource",
            "withEntity",
            "withPosition",
            "withRotation",
            "withCallback",
            "withSuppressedOutput",
            "withPermission",
            "withMaximumPermission",
            "withAnchor",
            "withLevel",
            "withSigningContext",
            "withChatMessageChainer",
            "withReturnValueConsumer"
    }, at = @At("RETURN"))
    private CommandSourceStack copyExtraData(CommandSourceStack orig) {
        ((JFLibCommandSourceStackDuck) orig).jackfredlib$setData(this.extraData);
        return orig;
    }

    @Override
    public void jackfredlib$setData(Map<ResourceLocation, ExtraSourceData<?>> data) {
        this.extraData.clear();
        data.forEach((key, value) -> this.extraData.put(key, value.copy()));
    }

    @Override
    public <T extends ExtraSourceData<T>> T jackfredlib$getData(ExtraSourceData.Definition<T> definition) throws CommandSyntaxException {
        var data = extraData.computeIfAbsent(definition.id(), k -> definition.factory().get());
        if (definition.clazz().isInstance(data)) //noinspection unchecked
            return (T) data;
        else
            throw INVALID_DATA_TYPE.create(definition.clazz(), data.getClass());
    }
}

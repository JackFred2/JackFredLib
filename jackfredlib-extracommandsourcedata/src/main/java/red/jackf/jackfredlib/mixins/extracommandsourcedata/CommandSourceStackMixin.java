package red.jackf.jackfredlib.mixins.extracommandsourcedata;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.jackfredlib.api.extracommandsourcedata.ExtraSourceData;
import red.jackf.jackfredlib.impl.extracommandsourcedata.ExtraCommandSourceStackDataDuck;

import java.util.HashMap;
import java.util.Map;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin implements ExtraCommandSourceStackDataDuck {
    @Unique
    private final Map<ResourceLocation, ExtraSourceData<?>> jflibExtraData = new HashMap<>();

    @ModifyReturnValue(method = {
            "withSource",
            "withEntity",
            "withPosition",
            "withRotation", // handles facing
            "withCallback(Lnet/minecraft/commands/CommandResultCallback;)Lnet/minecraft/commands/CommandSourceStack;",
            "withSuppressedOutput",
            "withPermission",
            "withMaximumPermission",
            "withAnchor",
            "withLevel",
            "withSigningContext"
    }, at = @At("RETURN"))
    private CommandSourceStack jackfredlib$copyExtraData(CommandSourceStack orig) {
        ((ExtraCommandSourceStackDataDuck) orig).jackfredlib$setData(this.jflibExtraData);
        return orig;
    }

    @Override
    public void jackfredlib$setData(Map<ResourceLocation, ExtraSourceData<?>> data) {
        this.jflibExtraData.clear();
        data.forEach((key, value) -> this.jflibExtraData.put(key, value.copy()));
    }

    @Override
    public <T extends ExtraSourceData<T>> T jackfredlib$getData(ExtraSourceData.Definition<T> definition) throws CommandSyntaxException {
        var data = jflibExtraData.computeIfAbsent(definition.id(), k -> definition.factory().get());
        if (definition.clazz().isInstance(data))
            return definition.clazz().cast(data);
        else
            throw INVALID_DATA_TYPE.create(definition.clazz(), data.getClass());
    }
}

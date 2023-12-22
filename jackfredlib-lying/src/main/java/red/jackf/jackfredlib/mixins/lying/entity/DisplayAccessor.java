package red.jackf.jackfredlib.mixins.lying.entity;

import com.mojang.math.Transformation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setBillboardConstraints")
    void jflib$setBillboardConstraints(Display.BillboardConstraints constraints);

    @Invoker("setGlowColorOverride")
    void jflib$setGlowColorOverride(int colour);

    @Invoker("createTransformation")
    static Transformation jflib$createTransformation(SynchedEntityData data) {
        throw new AssertionError("mixin not applied correctly");
    }

    @Invoker("setTransformation")
    void jflib$setTransformation(Transformation transformation);

    @Invoker("setBrightnessOverride")
    void jflib$setBrightnessOverride(Brightness brightness);

    @Invoker("setViewRange")
    void jflib$setViewRange(float rangeModifier);

    @Invoker("setShadowRadius")
    void jflib$setShadowRadius(float radius);

    @Invoker("setShadowStrength")
    void jflib$setShadowStrength(float strength);

    @Invoker("setInterpolationDelay")
    void jflib$setTransformationInterpolationDelay(int ticks);

    @Invoker("setInterpolationDuration")
    void jflib$setTransformationInterpolationLength(int transformationInterpolationLength);

    // teleport interpolation: not applicable below 1.20.2
}

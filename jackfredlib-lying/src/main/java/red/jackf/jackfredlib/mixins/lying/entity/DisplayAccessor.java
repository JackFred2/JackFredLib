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
    void callSetBillboardConstraints(Display.BillboardConstraints constraints);

    @Invoker("setGlowColorOverride")
    void callSetGlowColorOverride(int colour);

    @Invoker("createTransformation")
    static Transformation callCreateTransformation(SynchedEntityData data) {
        throw new AssertionError("mixin not applied correctly");
    }

    @Invoker("setTransformation")
    void callSetTransformation(Transformation transformation);

    @Invoker("setBrightnessOverride")
    void callSetBrightnessOverride(Brightness brightness);

    @Invoker("setViewRange")
    void callSetViewRange(float rangeModifier);

    @Invoker("setShadowRadius")
    void callSetShadowRadius(float radius);

    @Invoker("setShadowStrength")
    void callSetShadowStrength(float radius);
}

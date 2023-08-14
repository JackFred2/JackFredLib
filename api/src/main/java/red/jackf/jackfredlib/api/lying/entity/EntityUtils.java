package red.jackf.jackfredlib.api.lying.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
    private EntityUtils() {}

    public static void setDisplayText(Display.TextDisplay display, Component text) {
        display.setText(text);
    }

    public static void setDisplayBlockState(Display.BlockDisplay display, BlockState state) {
        display.setBlockState(state);
    }

    public static void face(Entity source, Entity target) {
        face(source, target.getEyePosition());
    }

    public static void face(Entity source, Vec3 target) {
        var vector = target.subtract(source.position());
        var yRot = (float) (Mth.atan2(vector.z, vector.x) * 180.0F / (float) Math.PI) + 90.0F;
        //noinspection SuspiciousNameCombination
        var xRot = (float)(Mth.atan2(vector.horizontalDistance(), vector.y) * 180.0F / (float) Math.PI) - 90.0F;
        if (source instanceof Display) {
            yRot += 180.0F % 360.0F;
        }
        source.moveTo(source.getX(),
                source.getY(),
                source.getZ(),
                Mth.positiveModulo(yRot, 360f),
                Mth.positiveModulo(xRot, 360f));
        if (source instanceof LivingEntity living) {
            living.setYBodyRot(yRot);
            living.setYHeadRot(yRot);
        }
    }
}

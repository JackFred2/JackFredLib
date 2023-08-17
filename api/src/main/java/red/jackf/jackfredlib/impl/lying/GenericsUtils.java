package red.jackf.jackfredlib.impl.lying;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class GenericsUtils {
    private GenericsUtils() {}

    public static <E extends Entity> void leftClickEntity(ActiveEntityLie<E> activeEntityLie, boolean usingSecondaryAction) {
        activeEntityLie.lie().onLeftClick(activeEntityLie, usingSecondaryAction);
    }

    public static <E extends Entity> void rightClickEntity(ActiveEntityLie<E> activeEntityLie, boolean usingSecondaryAction, InteractionHand hand, Vec3 interactionLocation) {
        activeEntityLie.lie().onRightClick(activeEntityLie, usingSecondaryAction, hand, interactionLocation);
    }

    static <E extends Entity> void tickEntity(ActiveEntityLie<E> activeEntityLie) {
        activeEntityLie.lie().onTick(activeEntityLie);
    }
}

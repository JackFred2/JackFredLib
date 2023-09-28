package red.jackf.jackfredlib.impl.extracommandsourcedata;

import net.minecraft.commands.CommandSourceStack;

public class CommandSourceStackUtils {
    public static CommandSourceStack copy(CommandSourceStack original) {
        // cheap hack to create a new instance with the same values
        var oldPos = original.getPosition();
        return original.withPosition(oldPos.reverse()).withPosition(oldPos);
    }
}

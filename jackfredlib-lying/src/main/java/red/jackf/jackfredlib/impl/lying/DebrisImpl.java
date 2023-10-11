package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.api.lying.Lie;
import red.jackf.jackfredlib.impl.base.LogUtil;

public class DebrisImpl implements Debris {
    private static final Logger LOGGER = LogUtil.getLogger("Lies/Debris");
    public static final DebrisImpl INSTANCE = new DebrisImpl();

    private DebrisImpl() {}

    @Nullable
    private MinecraftServer server = null;
    private final Multimap<Long, Lie> scheduled = ArrayListMultimap.create();

    public void init(MinecraftServer server) {
        LOGGER.debug("Init debris");
        this.server = server;
    }

    public void deinit() {
        LOGGER.debug("Deinit debris");
        this.server = null;
        this.scheduled.clear();
    }

    public void tick() {
        if (this.server == null) return;
        var thisTick = scheduled.removeAll(server.overworld().getGameTime());
        if (thisTick.isEmpty()) return;
        LOGGER.debug("Fading {} lie(s) at {}", thisTick.size(), server.overworld().getGameTime());
        thisTick.forEach(Lie::fade);
    }

    @Override
    public void schedule(Lie lie, long lifetimeTicks) {
        if (this.server == null) {
            LOGGER.warn("Tried to use debris service when not in-game");
            return;
        }
        var targetTime = this.server.overworld().getGameTime() + lifetimeTicks;
        if (scheduled.values().remove(lie)) {
            LOGGER.debug("Updating target time for lie removal to " + targetTime);
        } else {
            LOGGER.debug("Scheduling lie for removal at " + targetTime);
        }
        scheduled.put(targetTime, lie);
    }

    @Override
    public void cancel(Lie lie) {
        if (this.server == null) {
            LOGGER.warn("Tried to use debris service when not in-game");
            return;
        }
        LOGGER.debug("Cancelling scheduled fade");
        scheduled.values().remove(lie);
    }
}

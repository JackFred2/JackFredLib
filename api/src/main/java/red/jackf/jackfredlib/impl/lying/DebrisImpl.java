package red.jackf.jackfredlib.impl.lying;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.jackfredlib.api.lying.ActiveLie;
import red.jackf.jackfredlib.api.lying.Debris;
import red.jackf.jackfredlib.impl.JackFredLib;

public class DebrisImpl implements Debris {
    private static final Logger LOGGER = JackFredLib.getLogger("Lies/Debris");
    public static final DebrisImpl INSTANCE = new DebrisImpl();

    private DebrisImpl() {}

    @Nullable
    private MinecraftServer server = null;
    private final Multimap<Long, ActiveLie<?>> scheduled = ArrayListMultimap.create();

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
        thisTick.forEach(ActiveLie::fade);
    }

    @Override
    public void schedule(ActiveLie<?> lie, long lifetime) {
        if (this.server == null) {
            LOGGER.warn("Tried to use debris service when not in-game");
            return;
        }
        var targetTime = this.server.overworld().getGameTime() + lifetime;
        LOGGER.debug("Scheduling lie for removal at " + targetTime);
        scheduled.put(targetTime, lie);
    }

    @Override
    public void cancel(ActiveLie<?> lie) {
        if (this.server == null) {
            LOGGER.warn("Tried to use debris service when not in-game");
            return;
        }
        LOGGER.debug("Cancelling scheduled fade");
        scheduled.values().remove(lie);
    }
}
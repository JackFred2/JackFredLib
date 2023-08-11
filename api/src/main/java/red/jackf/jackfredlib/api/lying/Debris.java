package red.jackf.jackfredlib.api.lying;

import red.jackf.jackfredlib.impl.lying.DebrisImpl;

/**
 * Service that allows you to forget lies after a set period automatically.
 */
public interface Debris {
    Debris INSTANCE = DebrisImpl.INSTANCE;

    void schedule(ActiveLie<?> lie, long lifetime);

    void cancel(ActiveLie<?> lie);
}

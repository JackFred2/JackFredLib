package red.jackf.jackfredlib.api.lying;

import red.jackf.jackfredlib.impl.lying.DebrisImpl;

/**
 * Service that allows you to forget lies after a set period automatically.
 */
public interface Debris {
    /**
     * Instance of the Debris manager. Use this to schedule fading.
     */
    Debris INSTANCE = DebrisImpl.INSTANCE;

    /**
     * <p>Schedule an active lie to be faded after a set amount of ticks. It is safe to 'fire-and-forget' using the Debris
     * services - if the lie was already faded by other means, then it will silently be ignored.</p>
     *
     * <p>If called for the same lie again, then the existing scheduled fade will be updated to use the new lifetime. This
     * allows you to 'refresh' a lie's lifetime.</p>
     * @param lie Lie to schedule a fade for
     * @param lifetimeTicks How many ticks this lie should live for
     */

    void schedule(ActiveLie<?> lie, long lifetimeTicks);

    /**
     * Cancel an active lie's scheduled fade.
     * @param lie Lie to cancel a scheduled fade for.
     */
    void cancel(ActiveLie<?> lie);
}

package red.jackf.jackfredlib.impl.lying.compat.imm_ptl;

import net.fabricmc.loader.api.FabricLoader;

public class Compatibility {
    public static final boolean IMM_PTL = FabricLoader.getInstance().isModLoaded("imm_ptl_core");
}

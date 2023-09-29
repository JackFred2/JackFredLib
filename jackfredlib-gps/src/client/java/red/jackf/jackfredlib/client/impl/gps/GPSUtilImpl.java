package red.jackf.jackfredlib.client.impl.gps;

import net.minecraft.client.Minecraft;
import red.jackf.jackfredlib.client.mixins.gps.PlayerTabOverlayAccessor;

import java.util.List;

public class GPSUtilImpl {
    public static List<String> getPlayerList() {
        var tabList = Minecraft.getInstance().gui.getTabList();
        return ((PlayerTabOverlayAccessor) tabList).jflib$getPlayerInfos().stream()
                .map(info -> tabList.getNameForDisplay(info).getString())
                .toList();
    }
}

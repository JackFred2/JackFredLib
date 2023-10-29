package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import red.jackf.jackfredlib.client.api.gps.Coordinate;

public class GPSCoordGrabber {
    public static void setup() {
        HudRenderCallback.EVENT.register((pose, tickDelta) -> GuiComponent.drawString(pose,
                                                                                  Minecraft.getInstance().font,
                                                                                  Coordinate.getCurrent().map(Coordinate::toString).orElse("<no coordinate>"),
                                                                                  10, 10, 0xFF_AAFFAA));
    }
}

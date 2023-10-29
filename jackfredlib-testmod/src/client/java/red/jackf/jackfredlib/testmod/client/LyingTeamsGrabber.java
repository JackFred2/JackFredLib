package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public class LyingTeamsGrabber {
    public static void setup() {
        HudRenderCallback.EVENT.register((pose, tickDelta) -> {
            var level = Minecraft.getInstance().level;
            if (level == null) return;
            int i = 35;
            GuiComponent.drawString(pose,
                                    Minecraft.getInstance().font,
                                    "Current teams (according to client):",
                                    10, 25, 0xFF_FFFFFF);
            for (var team : level.getScoreboard().getPlayerTeams()) {
                GuiComponent.drawString(pose,
                                    Minecraft.getInstance().font,
                                    team.getName(),
                                    10, i, team.getColor().isColor() ? team.getColor().getColor() : 0xFF_FFFFFF);
                i += 10;
            }
        });
    }
}

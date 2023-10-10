package red.jackf.jackfredlib.testmod.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class LyingTeamsGrabber {
    public static void setup() {
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
            var level = Minecraft.getInstance().level;
            if (level == null) return;
            int i = 35;
            graphics.drawString(Minecraft.getInstance().font,
                                "Current teams (according to client):",
                                10, 25, 0xFF_FFFFFF);
            for (var team : level.getScoreboard().getPlayerTeams()) {
                graphics.drawString(Minecraft.getInstance().font,
                                    team.getName(),
                                    10, i, team.getColor().isColor() ? team.getColor().getColor() : 0xFF_FFFFFF);
                i += 10;
            }
        });
    }
}

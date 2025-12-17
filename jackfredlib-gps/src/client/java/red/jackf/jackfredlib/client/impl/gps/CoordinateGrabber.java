package red.jackf.jackfredlib.client.impl.gps;

import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import red.jackf.jackfredlib.client.api.gps.Coordinate;
import red.jackf.jackfredlib.client.mixins.gps.MinecraftServerAccessor;

import java.util.Locale;
import java.util.Optional;

public class CoordinateGrabber {
    private static long lastRealmId = -1L;
    private static String lastRealmName = "Unknown Realm";

    public static void setLastRealm(RealmsServer server) {
        lastRealmId = server.id;
        lastRealmName = server.name;
    }

    public static Optional<Coordinate> get() {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener connection = mc.getConnection();
        if (connection == null || !connection.getConnection().isConnected()) return Optional.empty();

        // Singleplayer or LAN host
        if (mc.getSingleplayerServer() != null) {
            MinecraftServer server = mc.getSingleplayerServer();
            return Optional.of(new Coordinate.Singleplayer(
                    "singleplayer/" + Sanitizer.sanitize(((MinecraftServerAccessor) server).getStorageSource().getLevelId()),
                    I18n.get("menu.singleplayer") + ": " + server.getWorldData().getLevelName(),
                    ((MinecraftServerAccessor) server).getStorageSource().getLevelId()
            ));
        }

        ServerData serverData = mc.getCurrentServer();
        if (serverData == null) return Optional.empty();

        // Minecraft Realms
        if (mc.isConnectedToRealms()) {
            return Optional.of(new Coordinate.Realms(
                    "realms/" + Sanitizer.sanitize(StringUtils.leftPad(Long.toHexString(lastRealmId).toUpperCase(Locale.ROOT), 16, '0')),
                    I18n.get("menu.online") + ": " + lastRealmName
            ));
        // LAN server
        } else if (serverData.isLan()) {
            // LAN-FIX VERSION: motd can be null sometimes, fallback to server name or empty string
            String motd = serverData.motd != null ? serverData.motd.getString() : 
                         (serverData.name != null ? serverData.name : "LAN");
            // Extract world name from MOTD format "PlayerName - WorldName"
            // Use singleplayer prefix so LAN guests share the same Memory Bank as host
            String worldName = motd;
            int separatorIndex = motd.indexOf(" - ");
            if (separatorIndex != -1 && separatorIndex + 3 < motd.length()) {
                worldName = motd.substring(separatorIndex + 3);
            }
            return Optional.of(new Coordinate.Lan(
                    "singleplayer/" + Sanitizer.sanitize(worldName),
                    "LAN: " + motd
            ));
        // Multiplayer
        } else {
            return Optional.of(new Coordinate.Multiplayer(
                    "multiplayer/" + Sanitizer.sanitize(serverData.ip),
                    I18n.get("menu.multiplayer") + ": " + serverData.name,
                    serverData.ip,
                    serverData.name
            ));
        }
    }
}

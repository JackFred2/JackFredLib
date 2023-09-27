package red.jackf.jackfredlib.impl;

import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import red.jackf.jackfredlib.api.Coordinate;
import red.jackf.jackfredlib.mixins.gps.MinecraftServerAccessor;

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
                    I18n.get("menu.singleplayer") + ": " + server.name(),
                    ((MinecraftServerAccessor) server).getStorageSource().getLevelId()
            ));
        }

        ServerData serverData = mc.getCurrentServer();
        if (serverData == null) return Optional.empty();

        // Minecraft Realms
        if (serverData.isRealm()) {
            return Optional.of(new Coordinate.Realms(
                    "realms/" + Sanitizer.sanitize(StringUtils.leftPad(Long.toHexString(lastRealmId).toUpperCase(Locale.ROOT), 16, '0')),
                    I18n.get("menu.online") + ": " + lastRealmName
            ));
        // LAN server
        } else if (serverData.isLan()) {
            String motd = serverData.motd.getString();
            return Optional.of(new Coordinate.Lan(
                    "lan/" + Sanitizer.sanitize(motd),
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

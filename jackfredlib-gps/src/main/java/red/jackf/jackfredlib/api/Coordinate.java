package red.jackf.jackfredlib.api;

import red.jackf.jackfredlib.impl.CoordinateGrabber;

import java.util.Optional;

/**
 * A coordinate represents the current connection to a world or server.
 * <p>
 * Some coordinates have additional data you may find useful; use <code>instanceof Singleplayer singleplayer</code> or
 * switch case pattern matching when Java 21 comes out
 */
public sealed interface Coordinate permits Coordinate.Singleplayer, Coordinate.Lan, Coordinate.Realms, Coordinate.Multiplayer {

    /**
     * Attempt to get a coordinate from the client's current state.
     *
     * @return An optional containing a coordinate representing the current connection state, or an empty optional if not
     * applicable (such as in the main menu).
     */
    static Optional<Coordinate> getCurrent() {
        return CoordinateGrabber.get();
    }

    /**
     * A unique ID for this coordinate. This ID is sanitized to be safe to use as a file name.
     *
     * @return A unique ID for this coordinate.
     */
    String id();

    /**
     * A user-friendly display name. This is prefixed with a descriptor which is localized based on the user's game language.
     *
     * @return A user-friendly display name for this coordinate.
     */
    String userFriendlyName();

    /**
     * A coordinate representing a loaded single player world. This is also used if the user is hosting a LAN world.
     *
     * @param id Derived from the world's folder name.
     *           Example: 'singeplayer/New World'
     * @param userFriendlyName Derived from the user-chosen name for this world.
     *                         English example: 'Singleplayer: New World'.
     * @param worldName The user-chosen name for this world.
     */
    record Singleplayer(String id, String userFriendlyName, String worldName) implements Coordinate {}

    /**
     * A coordinate representing a connection to a remote LAN world. This is not used if the user is hosting; see {@link Singleplayer}.
     * <p>
     * The ID is derived from the MOTD (host username + world name) and not the IP address as I consider it less likely
     * to change over time.
     *
     * @param id Derived from the MOTD message.
     *           Example: 'lan/Herobrine - New World (3)'.
     * @param userFriendlyName Derived from the MOTD message.
     *                         Example: 'LAN: Herobrine - New World (3)'
     */
    record Lan(String id, String userFriendlyName) implements Coordinate {}

    /**
     * A coordinate representing a connection to a realms server.
     *
     * @param id Derived from the realm's unique ID, formatted as a hexadecimal string.
     *           Example: 'realms/4201C8930F12E800'.
     * @param userFriendlyName Derived from the realm's name, as set by the owner.
     *                         English example: 'Minecraft Realms: Builder's Palace'.
     */
    record Realms(String id, String userFriendlyName) implements Coordinate {}

    /**
     * A coordinate representing a connection to a multiplayer server.
     *
     * @param id Derived from the server IP.
     *           Example: 'multiplayer/mc_hypixel_net'
     * @param userFriendlyName Derived from the user-chosen name on the server list.
     *                         English example: 'Multiplayer: Hypixel'
     * @param address Unsanitized IP address of the server.
     * @param serverEntryName User-chosen name on the server list.
     */
    record Multiplayer(String id, String userFriendlyName, String address, String serverEntryName) implements Coordinate {}
}

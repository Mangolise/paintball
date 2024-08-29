package net.mangolise.paintball;

import net.mangolise.gamesdk.limbo.Limbo;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;

import java.util.List;

// This is a dev server, not used in production
public class Test {

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setUuidProvider((connection, username) -> GameSdkUtils.createFakeUUID(username));

        InitRayfast.init();

        // initialise the dimension type
        var ignored = DimensionTypes.FULLBRIGHT;

        Limbo.waitForPlayers(2)
                .thenAccept(playerSet -> {
                    List<Player> players = List.copyOf(playerSet);

                    PaintballGame.Config config = MapConfigs.SHRINE.configCreator().apply(players);
                    PaintballGame game = new PaintballGame(config);

                    try {
                        game.setup();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .exceptionally(throwable -> {
            throw new RuntimeException(throwable);
        });

        if (GameSdkUtils.useBungeeCord()) {
            BungeeCordProxy.enable();
        }

        server.start("0.0.0.0", GameSdkUtils.getConfiguredPort());
    }
}

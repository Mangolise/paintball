package net.mangolise.paintball;

import dev.emortal.rayfast.vector.Vector3d;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.limbo.Limbo;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.extras.bungee.BungeeCordProxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

// This is a dev server, not used in production
public class Test {
    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setUuidProvider((connection, username) -> GameSdkUtils.createFakeUUID(username));

        InitRayfast.init();

        Limbo.waitForPlayers(2)
                .thenAccept(playerSet -> {
                    List<Player> players = List.copyOf(playerSet);

                    PaintballGame.Config config = new PaintballGame.Config(2, List.of(
                            new PaintballGame.Team(
                                    NamedTextColor.RED,
                                    new Pos(-12.5, 65, 13.5, -135, 0),
                                    Set.of(players.get(0).getUuid())
                            ),
                            new PaintballGame.Team(
                                    NamedTextColor.BLUE,
                                    new Pos(13.5, 65, -12.5, 45, 0),
                                    Set.of(players.get(1).getUuid())
                            )
                    ), Map.of());

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

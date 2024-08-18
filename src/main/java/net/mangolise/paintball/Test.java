package net.mangolise.paintball;

import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.limbo.Limbo;
import net.mangolise.gamesdk.util.Util;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

// This is a dev server, not used in production
public class Test {
    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setUuidProvider((connection, username) -> Util.createFakeUUID(username));

        Limbo.waitForPlayers(2)
                .thenAccept(players -> {
                    Player[] playersArray = players.toArray(new Player[0]);

                    PaintballGame.Config config = new PaintballGame.Config(2, List.of(
                            new PaintballGame.Team(
                                    NamedTextColor.RED,
                                    new Pos(-12.5, 65, 13.5, -135, 0),
                                    Set.of(playersArray[0].getUuid())
                            ),
                            new PaintballGame.Team(
                                    NamedTextColor.BLUE,
                                    new Pos(13.5, 65, -12.5, 45, 0),
                                    Set.of(playersArray[1].getUuid())
                            )
                    ), Map.of());

                    PaintballGame game = new PaintballGame(config);
                    game.start(players);
                })
                .exceptionally(throwable -> {
            throw new RuntimeException(throwable);
        });

        server.start("0.0.0.0", Util.getConfiguredPort());
    }
}

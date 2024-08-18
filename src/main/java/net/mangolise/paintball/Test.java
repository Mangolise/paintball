package net.mangolise.paintball;

import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.limbo.Limbo;
import net.mangolise.gamesdk.util.Util;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// This is a dev server, not used in production
public class Test {
    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setUuidProvider((connection, username) -> Util.createFakeUUID(username));

        PaintballGame.Config config = new PaintballGame.Config(2, List.of(
                new PaintballGame.Team(
                        NamedTextColor.RED,
                        new Pos(-12.5, 65, 13.5, -135, 0),
                        Set.of(UUID.fromString("e403c681-5e80-3c6f-b6ba-4be584d5fb05"))
                ),
                new PaintballGame.Team(
                        NamedTextColor.BLUE,
                        new Pos(13.5, 65, -12.5, 45, 0),
                        Set.of(UUID.fromString("6c1bfc70-2bee-3e4b-8cfe-f0bb1c835b98"))
                )
        ), Map.of());

        PaintballGame game = new PaintballGame(config);

        Limbo.waitForPlayers(config.playerCount())
                .thenAccept(game::start)
                .exceptionally(throwable -> {
            throw new RuntimeException(throwable);
        });

        server.start("0.0.0.0", Util.getConfiguredPort());
    }
}

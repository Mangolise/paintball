package net.mangolise.paintball;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public enum MapConfigs {
    FRUIT(players -> new PaintballGame.Config(2, List.of(
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
    ), null, "fruit", Map.of())),

    SPIKES(players -> new PaintballGame.Config(2, List.of(
            new PaintballGame.Team(
                    NamedTextColor.RED,
                    new Pos(-9.5, -28, 1.5, -90, 0),
                    Set.of(players.get(0).getUuid())
            ),
            new PaintballGame.Team(
                    NamedTextColor.BLUE,
                    new Pos(35.5, -28, 1.5, 90, 0),
                    Set.of(players.get(1).getUuid())
            )
    ), null, "spikes", Map.of())),

    SHRINE(players -> new PaintballGame.Config(2, List.of(
            new PaintballGame.Team(
                    NamedTextColor.RED,
                    new Pos(8, 1, 0, -90, 0),
                    Set.of(players.get(0).getUuid())
            ),
            new PaintballGame.Team(
                    NamedTextColor.BLUE,
                    new Pos(0, 1, 8, 90, 0),
                    Set.of(players.get(1).getUuid())
            )
    ), DimensionTypes.FULLBRIGHT, "shrine", Map.of())),

    ;

    private final Function<List<Player>, PaintballGame.Config> config;

    MapConfigs(Function<List<Player>, PaintballGame.Config> config) {
        this.config = config;
    }

    public Function<List<Player>, PaintballGame.Config> configCreator() {
        return config;
    }
}

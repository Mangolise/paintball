package net.mangolise.paintball;

import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.log.Log;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.mangolise.paintball.entity.ModelEntity;
import net.mangolise.paintball.entity.ModelWrapper;
import net.mangolise.paintball.event.PlayerDamagePlayerEvent;
import net.mangolise.paintball.event.PlayerUseWeaponEvent;
import net.mangolise.paintball.feature.DeathFeature;
import net.mangolise.paintball.feature.DeathToastFeature;
import net.mangolise.paintball.weapon.UseWeaponFeature;
import net.mangolise.paintball.weapon.Weapon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.tag.Tag;
import net.minestom.server.world.DimensionType;
import net.worldseed.multipart.ModelEngine;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.AnimationHandlerImpl;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PaintballGame extends BaseGame<PaintballGame.Config> {

    public static final Tag<Instance> INSTANCE_TAG = Tag.Transient("instance");

    public PaintballGame(Config config) {
        super(config);
    }

    @Override
    public void setup() {
        Instance instance;
        {
            PolarLoader loader = GameSdkUtils.getPolarLoaderFromResource("worlds/" + config.map() + ".polar");
            if (config.dimension == null) {
                instance = MinecraftServer.getInstanceManager().createInstanceContainer(loader);
            } else {
                instance = MinecraftServer.getInstanceManager().createInstanceContainer(config.dimension, loader);
            }
            instance.enableAutoChunkLoad(true);
            setTag(INSTANCE_TAG, instance);
        }

        Log.logger().info("Starting Paintball game with {} players", MinecraftServer.getConnectionManager().getOnlinePlayers().size());

        super.setup();

        Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();

        // Set up teams
        Map<Player, Team> player2Team = players.stream()
                .map(player -> {
                    UUID uuid = player.getUuid();
                    Team team = config.teams().stream()
                            .filter(t -> t.players().contains(uuid))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Player " + player.getUsername() + " (" + uuid + ") is not in a team"));
                    return Map.entry(player, team);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        try {
            ModelEngine.loadMappings(new FileReader("resourcepack/model_mappings.json"), Path.of("resourcepack/models"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        player2Team.forEach((player, team) -> {
            player.setTag(Team.TAG, team);
            player.setRespawnPoint(team.spawnPoint());
            player.setGameMode(GameMode.ADVENTURE);
            player.setInstance(instance, team.spawnPoint());

            player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(1000);
            player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(1000);
            for (int i = 0; i < Weapon.values().length; i++) {
                player.getInventory().setItemStack(i, Weapon.values()[i].displayItem());
            }

            ModelEntity modelEntity = new ModelEntity("luger", instance, new Pos(0, 0, 0));
            player.addPassenger(modelEntity);

            player.eventNode().addListener(PlayerMoveEvent.class, event -> {
                modelEntity.teleport(modelEntity.getPosition().withView(event.getNewPosition()));
            });

            player.eventNode().addListener(PlayerUseWeaponEvent.class, event -> {
                modelEntity.animations().playOnce("reload", () -> {});
            });
        });
    }

    @Override
    public List<Feature<?>> features() {
        return List.of(
                new UseWeaponFeature(),
                new DeathFeature(5),
                new DeathToastFeature()
        );
    }

    public record PlayerConfig(Set<UUID> unlockedWeapons) {
    }

    public record Team(NamedTextColor teamColor, Pos spawnPoint, Set<UUID> players) {
        public static final Tag<Team> TAG = Tag.Transient("team");
    }

    /**
     * @param playerCount the player count of the game. The game starts when this many players have joined
     * @param teams the teams in the game
     */
    public record Config(int playerCount, List<Team> teams, @Nullable DynamicRegistry.Key<DimensionType> dimension, String map, Map<UUID, PlayerConfig> players) {
    }
}

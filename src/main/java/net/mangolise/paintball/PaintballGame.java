package net.mangolise.paintball;

import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.log.Log;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.mangolise.paintball.weapon.UseWeaponFeature;
import net.mangolise.paintball.weapon.Weapon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class PaintballGame extends BaseGame<PaintballGame.Config> {

    public static final Tag<Instance> INSTANCE_TAG = Tag.Transient("instance");

    public PaintballGame(Config config) {
        super(config);
    }

    @Override
    public void setup() {
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(GameSdkUtils.getPolarLoaderFromResource("worlds/fruit.polar"));
        instance.enableAutoChunkLoad(true);
        setTag(INSTANCE_TAG, instance);

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

        player2Team.forEach((player, team) -> {
            player.setTag(Team.TAG, team);
            player.setRespawnPoint(team.spawnPoint());
            player.setGameMode(GameMode.ADVENTURE);
            player.setInstance(instance, team.spawnPoint());

            player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(1000);
            player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(1000);

            player.getInventory().setItemStack(0, Weapon.FROUP_DE_FROUP.displayItem());

//            player.eventNode().addListener(PlayerPacketEvent.class, event -> {
//                ClientPacket packet = event.getPacket();
//                switch (packet) {
//                    case ClientPlayerPositionPacket ignored -> {}
//                    case ClientPlayerPositionAndRotationPacket ignored -> {}
//                    case ClientPlayerRotationPacket ignored -> {}
//                    case ClientKeepAlivePacket ignored -> {}
//                    default -> Log.logger().info("Player {} sent packet {}", player.getUsername(), event.getPacket());
//                }
//            });
        });
    }

    @Override
    public List<Feature<?>> features() {
        return List.of(new UseWeaponFeature(), new DeathFeature(true));
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
    public record Config(int playerCount, List<Team> teams, Map<UUID, PlayerConfig> players) {
    }
}

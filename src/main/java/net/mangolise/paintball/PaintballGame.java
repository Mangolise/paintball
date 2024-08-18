package net.mangolise.paintball;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.log.Log;
import net.mangolise.paintball.weapon.Weapon;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.tag.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class PaintballGame extends BaseGame<PaintballGame.Config> {

    public PaintballGame(Config config) {
        super(config);
    }

    @Override
    public void setup() {
        //super.setup();

        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader("worlds/fruit"));
        instance.enableAutoChunkLoad(true);

        Log.logger().info("Starting Paintball game with {} players", MinecraftServer.getConnectionManager().getOnlinePlayers().size());

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

            player.getInventory().setItemStack(0, Weapon.GOOP_DE_GOOP.displayItem());

            player.eventNode().addListener(PlayerPacketEvent.class, event -> {
                if (!(event.getPacket() instanceof ClientInteractEntityPacket packet)) return;
                if (!(packet.type() instanceof ClientInteractEntityPacket.InteractAt entityInteractAt)) return;
                // we only care about one of the two hands
                if (entityInteractAt.hand() != Player.Hand.MAIN) return;

                int targetId = packet.targetId();
                Entity entity = instance.getEntityById(targetId);
                if (!(entity instanceof Player target)) return;

                // check if the player is holding a weapon
                if (player.getInventory().getItemInMainHand().isAir()) return;
                ItemStack item = player.getInventory().getItemInMainHand();
                Weapon weapon = Weapon.weaponFromItemStack(item);
                if (weapon == null) return;

                weapon.action().execute(new PlayerWeaponContext(this, player, target));
                player.sendMessage(Component.text("You hit " + target.getUsername()));
            });

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
        return List.of();
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

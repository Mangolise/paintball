package net.mangolise.paintball;

import net.mangolise.gamesdk.Game;
import net.mangolise.gamesdk.util.Timer;
import net.mangolise.paintball.event.PlayerDamagePlayerEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public record DeathFeature(@Nullable Integer spectatorTime) implements Game.Feature<Game> {
    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(EntityDamageEvent.class, event -> {
            Entity entity = event.getEntity();
            if (!(entity instanceof Player player)) return;

            if (player.getHealth() - event.getDamage().getAmount() <= 0) {
                double damageNeededToDie = player.getHealth();

                if (spectatorTime != null) {
                    event.setCancelled(true);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.heal();

                    Timer.countDownForPlayer(spectatorTime, player).thenRun(() -> {
                        player.teleport(player.getRespawnPoint())
                                .thenRun(() -> player.setGameMode(GameMode.ADVENTURE));
                    });
                } else {
                    player.kill();
                }

                if (event.getDamage().getAttacker() instanceof Player attacker) {
                    EventDispatcher.call(new PlayerDamagePlayerEvent(attacker, player, damageNeededToDie, true));
                }
            } else {
                // non-death
                if (event.getDamage().getAttacker() instanceof Player attacker) {
                    EventDispatcher.call(new PlayerDamagePlayerEvent(attacker, player, event.getDamage().getAmount(), false));
                }
            }
        });
    }
}

package net.mangolise.paintball;

import net.mangolise.gamesdk.Game;
import net.mangolise.paintball.event.PlayerDamagePlayerEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityDamageEvent;

public record DeathFeature(boolean instantRespawn) implements Game.Feature<Game> {
    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(EntityDamageEvent.class, event -> {
            Entity entity = event.getEntity();
            if (!(entity instanceof Player player)) return;

            if (player.getHealth() - event.getDamage().getAmount() < 0) {
                double damageNeededToDie = player.getHealth();

                event.setCancelled(true);
                if (instantRespawn) {
                    event.setCancelled(true);
                    player.teleport(player.getRespawnPoint());
                    player.heal();
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

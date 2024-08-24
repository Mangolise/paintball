package net.mangolise.paintball;

import net.mangolise.gamesdk.Game;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDamageEvent;

public record DeathFeature(boolean instantRespawn) implements Game.Feature<Game> {
    @Override
    public void setup(Context<Game> context) {
        MinecraftServer.getGlobalEventHandler().addListener(EntityDamageEvent.class, event -> {
            Entity entity = event.getEntity();
            if (!(entity instanceof Player player)) return;

            if (player.getHealth() - event.getDamage().getAmount() <= 0) {
                if (instantRespawn) {
                    player.teleport(player.getRespawnPoint());
                    player.heal();
                    event.setCancelled(true);
                    // TODO: Add death handling
                }
            }
        });
    }
}

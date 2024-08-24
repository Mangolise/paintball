package net.mangolise.paintball.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.trait.PlayerEvent;

/**
 * Called when a player damages another player.
 * This event differs from {@link EntityDamageEvent} in that it is also called when a player kills another player.
 * When a player kills another player, this event only contains the minimum damage required to kill the player.
 */
public class PlayerDamagePlayerEvent implements PlayerEvent {

    private final Player player;
    private final Player target;
    private final double damage;
    private final boolean isDeath;

    public PlayerDamagePlayerEvent(Player player, Player target, double damage, boolean isDeath) {
        this.player = player;
        this.target = target;
        this.damage = damage;
        this.isDeath = isDeath;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public Player getTarget() {
        return target;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isDeath() {
        return isDeath;
    }
}

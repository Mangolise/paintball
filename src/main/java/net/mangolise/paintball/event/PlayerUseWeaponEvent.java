package net.mangolise.paintball.event;

import net.mangolise.paintball.weapon.Weapon;
import net.mangolise.paintball.weapon.WeaponAction;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerUseWeaponEvent implements PlayerEvent {

    private final Player player;
    private final Weapon weapon;
    private final WeaponAction.Context context;

    public PlayerUseWeaponEvent(Player player, Weapon weapon, WeaponAction.Context context) {
        this.player = player;
        this.weapon = weapon;
        this.context = context;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Weapon getWeapon() {
        return weapon;
    }

    public @Nullable WeaponAction.Context getContext() {
        return context;
    }
}

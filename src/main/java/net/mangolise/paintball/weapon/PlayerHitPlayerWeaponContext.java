package net.mangolise.paintball.weapon;

import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

record PlayerHitPlayerWeaponContext(PaintballGame game, Player player, Player target, Point hitPosition) implements WeaponAction.HitPlayerContext {

    @Override
    public Instance instance() {
        return player.getInstance();
    }

    @Override
    public Pos eyePosition() {
        return player.getPosition().add(0, player.getEyeHeight(), 0);
    }
}

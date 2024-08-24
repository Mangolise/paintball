package net.mangolise.paintball.weapon;

import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

record PlayerMissWeaponContext(PaintballGame game, Player player, Point hitPosition) implements WeaponAction.MissContext {

    @Override
    public Instance instance() {
        return player.getInstance();
    }

    @Override
    public Pos eyePosition() {
        return player.getPosition().add(0, player.getEyeHeight(), 0);
    }
}

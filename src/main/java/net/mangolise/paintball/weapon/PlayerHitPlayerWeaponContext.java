package net.mangolise.paintball.weapon;

import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.TagHandler;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;

record PlayerHitPlayerWeaponContext(PaintballGame game, Player player, Player target, Point hitPosition, TagHandler tagHandler) implements WeaponAction.HitPlayerContext {

    public PlayerHitPlayerWeaponContext(PaintballGame game, Player player, Player target, Point hitPosition) {
        this(game, player, target, hitPosition, TagHandler.newHandler());
    }

    @Override
    public Instance instance() {
        return player.getInstance();
    }

    @Override
    public Pos eyePosition() {
        return player.getPosition().add(0, player.getEyeHeight(), 0);
    }
}

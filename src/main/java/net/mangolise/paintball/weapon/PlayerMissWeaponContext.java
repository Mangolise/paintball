package net.mangolise.paintball.weapon;

import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.TagHandler;

record PlayerMissWeaponContext(PaintballGame game, Player player, Point hitPosition, TagHandler tagHandler) implements WeaponAction.MissContext {

    public PlayerMissWeaponContext(PaintballGame game, Player player, Point hitPosition) {
        this(game, player, hitPosition, TagHandler.newHandler());
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

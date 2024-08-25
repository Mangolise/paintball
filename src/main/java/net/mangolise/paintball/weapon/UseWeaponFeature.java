package net.mangolise.paintball.weapon;

import net.kyori.adventure.text.Component;
import net.mangolise.gamesdk.Game;
import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;

public record UseWeaponFeature() implements Game.Feature<PaintballGame> {
    @Override
    public void setup(Context<PaintballGame> context) {
        PaintballGame game = context.game();
        Instance instance = game.getTag(PaintballGame.INSTANCE_TAG);

        instance.eventNode().addListener(PlayerPacketEvent.class, event -> {
            Player player = event.getPlayer();

            if (!(event.getPacket() instanceof ClientInteractEntityPacket packet)) return;
            if (!(packet.type() instanceof ClientInteractEntityPacket.InteractAt entityInteractAt)) return;
            // we only care about one of the two hands
            if (entityInteractAt.hand() != Player.Hand.MAIN) return;

            int targetId = packet.targetId();
            Entity entity = instance.getEntityById(targetId);
            if (!(entity instanceof Player target)) return;
            Vec hitOffset = new Vec(entityInteractAt.targetX(), entityInteractAt.targetY(), entityInteractAt.targetZ());
            Vec hitPosition = hitOffset.add(target.getPosition());

            // check if the player is holding a weapon
            if (player.getInventory().getItemInMainHand().isAir()) return;
            ItemStack item = player.getInventory().getItemInMainHand();
            Weapon weapon = Weapon.weaponFromItemStack(item);
            if (weapon == null) return;

            // if the player is holding a weapon, use it
            weapon.action().execute(new PlayerHitPlayerWeaponContext(game, player, target, hitPosition));
        });

        instance.eventNode().addListener(PlayerPacketEvent.class, event -> {
            Player player = event.getPlayer();

            if (!(event.getPacket() instanceof ClientPlayerBlockPlacementPacket blockPlace)) return;

            // check if the player is holding a weapon
            if (player.getInventory().getItemInMainHand().isAir()) return;
            ItemStack item = player.getInventory().getItemInMainHand();
            Weapon weapon = Weapon.weaponFromItemStack(item);
            if (weapon == null) return;

            // if the player is holding a weapon, use it
            Point hitPosition = blockPlace.blockPosition().add(blockPlace.cursorPositionX(), blockPlace.cursorPositionY(), blockPlace.cursorPositionZ());
            weapon.action().execute(new PlayerMissWeaponContext(game, player, Vec.fromPoint(hitPosition)));
        });

    }
}

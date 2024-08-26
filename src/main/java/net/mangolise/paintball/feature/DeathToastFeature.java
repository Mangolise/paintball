package net.mangolise.paintball.feature;

import net.kyori.adventure.text.Component;
import net.mangolise.gamesdk.Game;
import net.mangolise.paintball.PaintballGame;
import net.mangolise.paintball.event.PlayerDamagePlayerEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.notifications.Notification;
import net.minestom.server.advancements.notifications.NotificationCenter;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

public class DeathToastFeature implements Game.Feature<PaintballGame> {
    @Override
    public void setup(Context<PaintballGame> context) {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDamagePlayerEvent.class, event -> {
            if (!event.isDeath()) return;
            Player attacker = event.getPlayer();
            Player target = event.getTarget();

            Component text = Component.text(attacker.getUsername() + " killed " + target.getUsername());
            Notification deathNotification = new Notification(text, FrameType.TASK, Material.NETHERITE_SWORD);
            NotificationCenter.send(deathNotification, MinecraftServer.getConnectionManager().getOnlinePlayers());
        });
    }
}

package net.mangolise.paintball.util;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.tag.Tag;

import java.util.List;
import java.util.stream.Stream;

public class PaintballUtils {

    /** Unix timestamp in milliseconds */
    public static final Tag<Long> WEAPON_COOLDOWN = Tag.Long("weapon_cooldown").defaultValue(0L);

    public static void setWeaponCooldown(Player player, double seconds) {
        player.setTag(WEAPON_COOLDOWN, System.currentTimeMillis() + (long) (seconds * 1000.0));
        List<Material> mats = Stream.of(player.getInventory().getItemStacks())
                .map(ItemStack::material)
                .filter(mat -> mat != Material.AIR)
                .toList();

        List<SendablePacket> setCooldownPackets = mats.stream()
                .<SendablePacket>map(mat -> new SetCooldownPacket(mat.id(), (int) (seconds * 20)))
                .toList();

        player.sendPackets(setCooldownPackets);
    }

    public static boolean hasWeaponCooldown(Player player) {
        return System.currentTimeMillis() < player.getTag(WEAPON_COOLDOWN);
    }
}

package net.mangolise.paintball.weapon;

import net.minestom.server.tag.Tag;

public interface WeaponTags {
    Tag<Double> DAMAGE_TAG = Tag.Double("damage").defaultValue(1.0);
    Tag<Double> COMBO_TAG = Tag.Double("combo").defaultValue(1.0);
}

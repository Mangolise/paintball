package net.mangolise.paintball.weapon;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.particle.Particle;
import net.minestom.server.tag.Tag;
import static net.mangolise.paintball.weapon.WeaponAction.*;

import java.util.Locale;

public enum Weapon {
    FROUP_DE_FROUP(
        Component.text("Froup de Froup"),
        ItemStack.of(Material.FLINT_AND_STEEL),
        new Actions.SetDamage(1.5),

        onHit(new Actions.HitscanParticle(Particle.FLAME, 2)),
        onMiss(new Actions.HitscanParticle(Particle.ASH, 2)),

        onHit(new Actions.SetCooldown(1.0 / 5.1)),
        onMiss(new Actions.SetCooldown(0.1)),

        onHit(new Actions.ApplyDamage())
    ),

    RAILORD(
        Component.text("Railord"),
        ItemStack.of(Material.RAIL),

        onHit(new Actions.HitscanParticle(Particle.SCULK_CHARGE, 0.75)),
        onMiss(new Actions.HitscanParticle(Particle.SMOKE, 0.1)),

        // modify and show combo
        onHit(new Actions.ModifyCombo(combo -> Math.min(8, combo + 1))),
        onMiss(new Actions.ModifyCombo(combo -> 0)),
        lazy(context -> new Actions.SetChargeBar(context.player().getTag(WeaponTags.COMBO_TAG) / 8f)),
        new Actions.ApplyCombo((damage, combo) -> damage * Math.pow(1.5, combo)),

        onHit(context -> {
            double combo = context.player().getTag(WeaponTags.COMBO_TAG);
            Sound sound = Sound.sound(builder -> builder
                    .type(Key.key("block.beacon.activate"))
                    .volume(1f - (1f / (float) (combo + 1)))
                    .source(Sound.Source.PLAYER)
                    .seed((int) (Math.random() * 1000))
                    .pitch(1.0f / ((float) combo * 0.2f)));
            context.player().playSound(sound);
        }),

        onMiss(new Actions.PlayerSound(Sound.sound(builder -> builder
                .type(Key.key("block.beacon.deactivate"))
                .volume(1f)
                .source(Sound.Source.PLAYER)
                .pitch(1.0f)))),

        onHit(new Actions.SetCooldown(1)),
        onMiss(new Actions.SetCooldown(3)),
        lazy(context -> new Actions.ExplosionOnKill(2.0, 1 + (int) Math.pow(context.player().getTag(WeaponTags.COMBO_TAG), 2))),

        // apply damage
        onHit(new Actions.ApplyDamage())
    ),
    ;

    private final Component displayName;
    private final ItemStack displayItem;
    private final WeaponAction action;

    private static final Tag<String> WEAPON_ID_TAG = Tag.String("weaponId");

    Weapon(Component displayName, ItemStack stack, WeaponAction... action) {
        this.displayName = displayName;
        this.displayItem = stack
                .withCustomName(displayName)
                .withTag(Tag.String("weaponId"), name().toLowerCase(Locale.ROOT));
        this.action = join(action);
    }

    public static Weapon weaponFromItemStack(ItemStack itemStack) {
        return valueOf(itemStack.getTag(WEAPON_ID_TAG).toUpperCase(Locale.ROOT));
    }

    public Component displayName() {
        return displayName;
    }

    public ItemStack displayItem() {
        return displayItem;
    }

    public WeaponAction action() {
        return action;
    }
}

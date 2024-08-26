package net.mangolise.paintball.weapon;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.mangolise.paintball.util.PaintballUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum Weapon {

    // TODO: Cleanup weapon handling.
    // - move PaintballUtils.setWeaponCooldown to WeaponAction.Context
    // - remove duplicate damage calculations
    // - unify damage deal handling
    FROUP_DE_FROUP(
        Component.text("Froup de Froup"),
        ItemStack.of(Material.FLINT_AND_STEEL),
        context -> {
            switch (context) {
                case WeaponAction.HitPlayerContext hit -> {
                    hit.shootParticles(Particle.FLAME, 2);
                    hit.target().damage(Damage.fromPlayer(context.player(), 2));
                }
                case WeaponAction.MissContext miss -> {
                    miss.shootParticles(Particle.ASH, 2);
                }
            }
        },
        null
    ),
    RAILORD(
        Component.text("Railord"),
        ItemStack.of(Material.RAIL),
        context -> {
            Tag<Integer> combo_tag = Tag.Integer("railord_combo").defaultValue(0);
            switch (context) {
                case WeaponAction.HitPlayerContext hit -> {
                    hit.shootParticles(Particle.SCULK_CHARGE, 0.75);
                    int combo = Math.min(hit.player().getTag(combo_tag), 8);

                    // double the damage every hit
                    hit.player().setTag(combo_tag, combo + 1);
                    Damage damage = Damage.fromPlayer(context.player(), (float) ((2.0 * Math.pow(1.5, combo)) - 1.0));
                    Damage nextDamage = Damage.fromPlayer(context.player(), (float) ((2.0 * Math.pow(1.5, combo + 1)) - 1.0));
                    hit.player().setLevel((int) nextDamage.getAmount());
                    hit.player().setExp((float) combo / 8f);

                    Sound sound = Sound.sound(builder -> builder
                            .type(Key.key("block.beacon.activate"))
                            .volume(1f - (1f / (float) (combo + 1)))
                            .source(Sound.Source.PLAYER)
                            .seed((int) (Math.random() * 1000))
                            .pitch(1.0f / ((float) combo * 0.2f)));
                    hit.player().playSound(sound);

                    // short cooldown
                    PaintballUtils.setWeaponCooldown(hit.player(), 1);

                    boolean willDie = hit.target().getHealth() - damage.getAmount() <= 0;
                    hit.target().damage(damage);

                    // send explosion if the player will die
                    if (willDie) {
                        ParticlePacket packet = new ParticlePacket(Particle.WHITE_SMOKE, hit.hitPosition(), Vec.ZERO, 0.1f, (int) Math.pow(combo, 2));
                        hit.instance().sendGroupedPacket(packet);
                    }
                }
                case WeaponAction.MissContext miss -> {
                    if (miss.player().getTag(combo_tag) == 0) return;

                    // reset the combo
                    miss.player().setTag(combo_tag, 0);
                    miss.player().setLevel(0);
                    miss.player().setExp(0);
                    miss.player().playSound(Sound.sound(builder -> builder
                            .type(Key.key("block.beacon.deactivate"))
                            .volume(1f)
                            .source(Sound.Source.PLAYER)
                            .pitch(1.0f)));
                    miss.shootParticles(Particle.SMOKE, 0.1);

                    // long cooldown
                    PaintballUtils.setWeaponCooldown(miss.player(), 3);
                }
            }
        },
        null
    ),
    ;

    private final Component displayName;
    private final ItemStack displayItem;
    private final WeaponAction action;

    private static final Tag<String> WEAPON_ID_TAG = Tag.String("weaponId");

    Weapon(Component displayName, ItemStack stack, WeaponAction action, @Nullable Runnable setup) {
        this.displayName = displayName;
        this.displayItem = stack
                .withCustomName(displayName)
                .withTag(Tag.String("weaponId"), name().toLowerCase(Locale.ROOT));
        this.action = action;
        if (setup != null) setup.run();
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

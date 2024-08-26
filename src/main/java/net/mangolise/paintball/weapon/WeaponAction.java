package net.mangolise.paintball.weapon;

import net.mangolise.gamesdk.entity.ProjectileEntity;
import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

public interface WeaponAction {
    void execute(Context context);

    sealed interface Context permits HitPlayerContext, MissContext {
        PaintballGame game();
        Player player();
        Instance instance();
        Pos eyePosition();

        Point hitPosition();

        default void shootParticles(Particle particle, double stepSize) {
            // shoot particles in the direction the player is looking
            Vec start = Vec.fromPoint(eyePosition());
            Vec end = Vec.fromPoint(hitPosition());

            {
                // we offset the start position a bit so the particles don't spawn in front of the player eyes
                Vec dir = end.sub(start).normalize();
                start = start.add(dir.rotateAroundY(-Math.PI / 4.0).mul(0.3));
                start = start.sub(0, 0.1, 0);
            }

            Vec step = end.sub(start).normalize().mul(stepSize);

            do {
                ParticlePacket packet = new ParticlePacket(particle, true, start.x(), start.y(), start.z(), 0, 0, 0, 0, 1);
                instance().sendGroupedPacket(packet);
                start = start.add(step);
            } while (start.distanceSquared(end) >= stepSize);
        }
    }

    sealed interface MissContext extends Context permits PlayerMissWeaponContext {
    }

    sealed interface HitPlayerContext extends Context permits PlayerHitPlayerWeaponContext {
        Player target();

        default void shootProjectile(Block display, double scale, double speed) {
            ProjectileEntity projectile = new ProjectileEntity(display, scale);
            projectile.setInstance(instance(), eyePosition());

            // add some velocity
            Vec velocity = eyePosition().direction().mul(speed);
            projectile.setVelocity(velocity);
        }
    }
}

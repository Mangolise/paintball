package net.mangolise.paintball.weapon;

import net.mangolise.paintball.PaintballGame;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Taggable;

import java.util.List;
import java.util.function.Function;

public interface WeaponAction {

    void execute(Context context);

    /** Only executes the action if the player hit another player */
    static WeaponAction onHit(WeaponAction action) {
        return new OnlyOnHit(action);
    }

    /** Only executes the action if the player missed */
    static WeaponAction onMiss(WeaponAction action) {
        return new OnlyOnMiss(action);
    }

    static WeaponAction join(WeaponAction... actions) {
        return new Join(List.of(actions));
    }

    /** Creates an action for every invocation */
    static WeaponAction lazy(Function<Context, WeaponAction> createAction) {
        return new LazyWeaponAction(createAction);
    }

    sealed interface Context extends Taggable permits HitPlayerContext, MissContext {
        PaintballGame game();
        Player player();
        Instance instance();
        Pos eyePosition();

        Point hitPosition();
    }

    sealed interface MissContext extends Context permits PlayerMissWeaponContext {
    }

    sealed interface HitPlayerContext extends Context permits PlayerHitPlayerWeaponContext {
        Player target();
    }
}

record Join(List<WeaponAction> actions) implements WeaponAction {
    @Override
    public void execute(Context context) {
        actions.forEach(action -> action.execute(context));
    }
}

record OnlyOnHit(WeaponAction action) implements WeaponAction {
    @Override
    public void execute(Context context) {
        if (context instanceof HitPlayerContext hit) {
            action.execute(hit);
        }
    }
}

record OnlyOnMiss(WeaponAction action) implements WeaponAction {
    @Override
    public void execute(Context context) {
        if (context instanceof MissContext miss) {
            action.execute(miss);
        }
    }
}

record LazyWeaponAction(Function<Context, WeaponAction> action) implements WeaponAction {
    @Override
    public void execute(Context context) {
        action.apply(context).execute(context);
    }
}

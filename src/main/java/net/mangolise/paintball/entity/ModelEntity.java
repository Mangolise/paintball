package net.mangolise.paintball.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.animations.AnimationHandler;
import net.worldseed.multipart.animations.AnimationHandlerImpl;
import org.jetbrains.annotations.NotNull;

public class ModelEntity extends Entity {

    private final ModelWrapper wrapper;
    private final AnimationHandler animations;

    public ModelEntity(String modelId, Instance instance, Pos pos) {
        super(EntityType.ITEM_DISPLAY);
        this.wrapper = new ModelWrapper(modelId + ".bbmodel", 1.0f);
        this.animations = new AnimationHandlerImpl(wrapper);

        wrapper.init(instance, pos);
        this.setInstance(instance, pos);
    }

    public ModelWrapper wrapper() {
        return wrapper;
    }

    public AnimationHandler animations() {
        return animations;
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        wrapper.setPosition(getPosition());
        wrapper.setGlobalRotation(getPosition().yaw());
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        wrapper.addViewer(player);
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        wrapper.removeViewer(player);
    }
}
